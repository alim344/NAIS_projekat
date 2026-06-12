package com.example.instructor_analytics.service;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.json.JsonData;
import com.example.instructor_analytics.model.VehicleDocument;
import com.example.instructor_analytics.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private  ElasticsearchOperations elasticsearchOperations;
    @Autowired
    private  RedisCacheService redisCacheService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public VehicleDocument saveVehicle(VehicleDocument vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public Optional<VehicleDocument> getVehicleById(String id) {
        return vehicleRepository.findById(id);
    }

    public Iterable<VehicleDocument> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public VehicleDocument updateVehicle(String id, VehicleDocument updated) {
        updated.setId(id);
        return vehicleRepository.save(updated);
    }

    public void deleteAll() {
        vehicleRepository.deleteAll();
    }

    public void deleteVehicle(String id) {
        vehicleRepository.deleteById(id);
    }


    /**
     * COMPLEX QUERY 2:
     * Vozila kojima registracija istice u narednih X dana
     * + filtriranje po statusu
     * + sortiranje po datumu isteka (rastuce)
     * + agregacija: broj vozila po statusu
     */
    @Cacheable(value = "vehicles", key = "'expiring:' + #daysAhead + ':' + (#status != null ? #status : 'all')", unless = "#result == null")
    public Map<String, Object> findVehiclesWithExpiringRegistration(
            int daysAhead,
            String status) {

        String today = LocalDate.now().format(DATE_FORMATTER);
        String futureDate = LocalDate.now().plusDays(daysAhead).format(DATE_FORMATTER);

        List<Query> filterQueries = new ArrayList<>();

        filterQueries.add(Query.of(q -> q
                .range(r -> r
                        .field("registrationExpiryDate")
                        .gte(JsonData.of(today))
                        .lte(JsonData.of(futureDate))
                )
        ));

        if (status != null && !status.isEmpty()) {
            filterQueries.add(Query.of(q -> q
                    .term(t -> t
                            .field("status")
                            .value(status)
                    )
            ));
        }

        BoolQuery boolQuery = BoolQuery.of(b -> b
                .filter(filterQueries)
        );

        SortOptions sort = SortOptions.of(so -> so
                .field(f -> f
                        .field("registrationExpiryDate")
                        .order(SortOrder.Asc)
                )
        );

        NativeQuery query = NativeQuery.builder()
                .withQuery(Query.of(q -> q.bool(boolQuery)))
                .withSort(sort)
                .withAggregation("countByStatus", Aggregation.of(a -> a
                        .terms(t -> t
                                .field("status")
                        )
                ))
                .withMaxResults(1000)
                .build();

        SearchHits<VehicleDocument> searchHits = elasticsearchOperations.search(
                query, VehicleDocument.class
        );

        ElasticsearchAggregations aggs = (ElasticsearchAggregations) searchHits.getAggregations();
        Map<String, Long> countByStatus = new HashMap<>();
        if (aggs != null) {
            aggs.aggregations().stream()
                    .filter(a -> a.aggregation().getName().equals("countByStatus"))
                    .findFirst()
                    .ifPresent(a -> {
                        a.aggregation().getAggregate().sterms().buckets().array()
                                .forEach(bucket -> countByStatus.put(bucket.key().stringValue(), bucket.docCount()));
                    });
        }

        List<Map<String, Object>> vehicleList = searchHits.getSearchHits().stream()
                .map(hit -> {
                    VehicleDocument v = hit.getContent();
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", v.getId());
                    map.put("registrationNumber", v.getRegistrationNumber());
                    map.put("brand", v.getBrand());
                    map.put("status", v.getStatus());
                    map.put("currentMileage", v.getCurrentMileage());
                    map.put("registrationExpiryDate", v.getRegistrationExpiryDate());
                    map.put("instructorName", v.getInstructorName());
                    map.put("instructorLastname", v.getInstructorLastname());
                    return map;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("daysAhead", daysAhead);
        response.put("statusFilter", status != null ? status : "all");
        response.put("totalVehiclesFound", searchHits.getTotalHits());
        response.put("countByStatus", countByStatus);
        response.put("vehicles", vehicleList);

        return response;
    }


    /**
     * COMPLEX QUERY 3:
     * Statistika vozila za odredjeni brend:
     * - Prikaz samo za brend koji korisnik unese
     * - Opcioni filteri: status, minimalna kilometraza
     * - Prosecna kilometraza, broj vozila, ukupna kilometraza
     * - Sortiranje: po totalnoj kilometrazi (opadajuce)
     */
    @Cacheable(value = "vehicles", key = "'stats:' + #brand + ':' + (#status != null ? #status : 'all') + ':' + (#minMileage != null ? #minMileage : 'none')", unless = "#result == null")
    public Map<String, Object> getVehicleStatisticsByBrand(
            String brand,
            String status,
            Integer minMileage) {

        List<Query> mustQueries = new ArrayList<>();
        List<Query> filterQueries = new ArrayList<>();

        mustQueries.add(Query.of(q -> q
                .match(m -> m
                        .field("brand")
                        .query(brand)
                )
        ));

        if (status != null && !status.isEmpty()) {
            filterQueries.add(Query.of(q -> q
                    .term(t -> t
                            .field("status")
                            .value(status)
                    )
            ));
        }

        if (minMileage != null) {
            filterQueries.add(Query.of(q -> q
                    .range(r -> r
                            .field("currentMileage")
                            .gte(JsonData.of(minMileage))
                    )
            ));
        }

        BoolQuery boolQuery = BoolQuery.of(b -> b
                .must(mustQueries)
                .filter(filterQueries)
        );

        SortOptions sort = SortOptions.of(so -> so
                .field(f -> f
                        .field("currentMileage")
                        .order(SortOrder.Asc)
                )
        );

        NativeQuery query = NativeQuery.builder()
                .withQuery(Query.of(q -> q.bool(boolQuery)))
                .withSort(sort)
                .withAggregation("avgMileage", Aggregation.of(a -> a
                        .avg(av -> av.field("currentMileage"))
                ))
                .withAggregation("totalMileage", Aggregation.of(a -> a
                        .sum(s -> s.field("currentMileage"))
                ))
                .withMaxResults(1000)
                .build();

        SearchHits<VehicleDocument> searchHits = elasticsearchOperations.search(
                query, VehicleDocument.class
        );

        ElasticsearchAggregations aggs = (ElasticsearchAggregations) searchHits.getAggregations();
        double avgMileage = 0;
        double totalMileage = 0;
        if (aggs != null) {
            avgMileage = aggs.aggregations().stream()
                    .filter(a -> a.aggregation().getName().equals("avgMileage"))
                    .findFirst()
                    .map(a -> a.aggregation().getAggregate().avg().value())
                    .orElse(0.0);

            totalMileage = aggs.aggregations().stream()
                    .filter(a -> a.aggregation().getName().equals("totalMileage"))
                    .findFirst()
                    .map(a -> a.aggregation().getAggregate().sum().value())
                    .orElse(0.0);
        }

        List<Map<String, Object>> vehicleList = searchHits.getSearchHits().stream()
                .map(hit -> {
                    VehicleDocument v = hit.getContent();
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", v.getId());
                    map.put("registrationNumber", v.getRegistrationNumber());
                    map.put("brand", v.getBrand());
                    map.put("status", v.getStatus());
                    map.put("currentMileage", v.getCurrentMileage());
                    map.put("registrationExpiryDate", v.getRegistrationExpiryDate());
                    map.put("instructorName", v.getInstructorName());
                    map.put("instructorLastname", v.getInstructorLastname());
                    return map;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("brand", brand);
        response.put("statusFilter", status != null ? status : "all");
        response.put("minMileageFilter", minMileage != null ? minMileage : "none");
        response.put("totalVehiclesFound", searchHits.getTotalHits());
        response.put("averageMileage", Math.round(avgMileage));
        response.put("totalMileage", (long) totalMileage);
        response.put("vehicles", vehicleList);

        return response;
    }
}
