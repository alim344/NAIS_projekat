package com.example.instructor_analytics.service;

import co.elastic.clients.elasticsearch._types.ScriptSortType;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.SumAggregate;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.json.JsonData;
import com.example.instructor_analytics.model.InstructorDocument;
import com.example.instructor_analytics.repository.InstructorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class InstructorService {

    @Autowired
    private InstructorRepository instructorRepository;
    @Autowired
    private  ElasticsearchOperations elasticsearchOperations;
    @Autowired
    private  RedisCacheService redisCacheService;

    public InstructorDocument saveInstructor(InstructorDocument instructor) {
        return instructorRepository.save(instructor);
    }

    public Optional<InstructorDocument> getInstructorById(String id) {
        return instructorRepository.findById(id);
    }

    public Iterable<InstructorDocument> getAllInstructors() {
        return instructorRepository.findAll();
    }

   public InstructorDocument updateInstructor(String id, InstructorDocument updated) {
        updated.setId(id);
        return instructorRepository.save(updated);
    }

    public void deleteAll() {
        instructorRepository.deleteAll();
    }

    public void deleteInstructor(String id) {
        instructorRepository.deleteById(id);
    }

    public long countInstructors() {
        return instructorRepository.count();
    }

    public InstructorDocument assignVehicleToInstructor(String instructorId, String registrationNumber) {
        Optional<InstructorDocument> optionalInstructor = instructorRepository.findById(instructorId);
        if (optionalInstructor.isEmpty()) {
            return null;
        }
        InstructorDocument instructor = optionalInstructor.get();
        instructor.setVehicleRegistrationNumber(registrationNumber);
        return instructorRepository.save(instructor);
    }

    public InstructorDocument removeVehicleFromInstructor(String instructorId) {
        Optional<InstructorDocument> optionalInstructor = instructorRepository.findById(instructorId);
        if (optionalInstructor.isEmpty()) {
            return null;
        }
        InstructorDocument instructor = optionalInstructor.get();
        instructor.setVehicleRegistrationNumber(null);
        return instructorRepository.save(instructor);
    }

    public InstructorDocument findByEmail(String email) {
        NativeQuery query = NativeQuery.builder()
                .withQuery(Query.of(q -> q
                        .term(t -> t
                                .field("email")
                                .value(email)
                        )
                ))
                .build();

        SearchHits<InstructorDocument> searchHits = elasticsearchOperations.search(
                query, InstructorDocument.class
        );

        return searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .findFirst()
                .orElse(null);
    }

    /**
     * COMPLEX QUERY 1:
     * Pronadji instruktore koji:
     * - imaju slobodna mesta
     * - ciji dokumenti sadrze zadani tekst
     * - licenca nije istekla
     * - filter po kategoriji
     * Sortiranje: po broju slobodnih mesta opadajuce
     * Agregacija: ukupan broj slobodnih mesta
     */
    @Cacheable(value = "instructors", key = "#category + '_' + #searchText + '_'", unless = "#result == null")
    public Map<String, Object> findAvailableInstructorsWithText(String searchText, String category) {

        List<Query> mustQueries = new ArrayList<>();
        List<Query> filterQueries = new ArrayList<>();

        if (searchText != null && !searchText.isEmpty()) {
            mustQueries.add(Query.of(q -> q
                    .match(m -> m
                            .field("documentTypes")
                            .query(searchText)
                            .fuzziness("AUTO")
                    )
            ));
        }

        filterQueries.add(Query.of(q -> q
                .script(s -> s
                        .script(sc -> sc
                                .inline(i -> i
                                        .source("doc['maxCapacity'].value > doc['currentCandidateCount'].value")
                                        .lang("painless")
                                )
                        )
                )
        ));

        String today = LocalDate.now().toString().replace("-", "");
        filterQueries.add(Query.of(q -> q
                .range(r -> r
                        .field("licenseExpiryDate")
                        .gt(JsonData.of(today))
                )
        ));

        if (category != null && !category.isEmpty()) {
            filterQueries.add(Query.of(q -> q
                    .term(t -> t
                            .field("categories")
                            .value(category)
                    )
            ));
        }

        BoolQuery boolQuery = BoolQuery.of(b -> b
                .must(mustQueries)
                .filter(filterQueries)
        );

        // sort br. slobodnih mesta
        SortOptions sort = SortOptions.of(so -> so
                .script(ss -> ss
                        .type(ScriptSortType.Number)
                        .script(sc -> sc
                                .inline(i -> i
                                        .source("doc['maxCapacity'].value - doc['currentCandidateCount'].value")
                                        .lang("painless")
                                )
                        )
                        .order(SortOrder.Desc)
                )
        );

        NativeQuery query = NativeQuery.builder()
                .withQuery(Query.of(q -> q.bool(boolQuery)))
                .withSort(sort)
                .withAggregation("totalFreeSpots", Aggregation.of(a -> a
                        .sum(s -> s
                                .script(sc -> sc
                                        .inline(i -> i
                                                .source("doc['maxCapacity'].value - doc['currentCandidateCount'].value")
                                                .lang("painless")
                                        )
                                )
                        )
                ))
                .build();

        SearchHits<InstructorDocument> searchHits = elasticsearchOperations.search(
                query, InstructorDocument.class
        );

        // agrg
        ElasticsearchAggregations aggs = (ElasticsearchAggregations) searchHits.getAggregations();
        double totalFreeSpots = 0;
        if (aggs != null) {
            SumAggregate sumAgg = aggs.aggregations().stream()
                    .filter(a -> a.aggregation().getName().equals("totalFreeSpots"))
                    .findFirst()
                    .map(a -> a.aggregation().getAggregate().sum())
                    .orElse(null);
            if (sumAgg != null) {
                totalFreeSpots = sumAgg.value();
            }
        }

        List<Map<String, Object>> instructors = searchHits.getSearchHits().stream()
                .map(hit -> {
                    InstructorDocument doc = hit.getContent();
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", doc.getId());
                    map.put("name", doc.getName());
                    map.put("lastName", doc.getLastName());
                    map.put("email", doc.getEmail());
                    map.put("maxCapacity", doc.getMaxCapacity());
                    map.put("freeSpots", doc.getMaxCapacity() - doc.getCurrentCandidateCount());
                    map.put("vehicleRegistrationNumber", doc.getVehicleRegistrationNumber());
                    map.put("documentTypes", doc.getDocumentTypes());
                    map.put("categories", doc.getCategories());
                    return map;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("category", category != null ? category : "all");
        response.put("searchText", searchText != null ? searchText : "none");
        response.put("totalFreeSpotsAggregation", totalFreeSpots);
        response.put("totalInstructorsFound", instructors.size());
        response.put("instructors", instructors);

        return response;
    }

    @Cacheable(value = "instructors", key = "#id", unless = "#result == null")
    public InstructorDocument findById(String id) {
        return elasticsearchOperations.get(id, InstructorDocument.class);
    }

    @CachePut(value = "instructors", key = "#result.id")
    public InstructorDocument save(InstructorDocument instructor) {
        return elasticsearchOperations.save(instructor);
    }

    @CacheEvict(value = "instructors", key = "#id")
    public void deleteById(String id) {
        elasticsearchOperations.delete(id, InstructorDocument.class);
    }
}

