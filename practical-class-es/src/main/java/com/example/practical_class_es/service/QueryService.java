package com.example.practical_class_es.service;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import com.example.practical_class_es.doc.CandidateAnalytics;
import com.example.practical_class_es.doc.PracticalClassLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QueryService {

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;


//---------------------------------------------------------------------------------
    public Map<String, Object> searchClassesByTextAndPerformance(
            String searchText,
            Integer minScore,
            Integer minKm,
            Integer maxKm,
            Boolean onlyCompleted) {

        List<Query> mustQueries = new ArrayList<>();

        // text search po instructorNote i route (sa fuzziness za greske)
        if (searchText != null && !searchText.isBlank()) {
            Query multiMatchQuery = Query.of(q -> q
                    .multiMatch(m -> m
                            .query(searchText)
                            .fields("instructorNote", "route")
                            .fuzziness("AUTO")
                    )
            );
            mustQueries.add(multiMatchQuery);
        }

        // Filter: samo zavrseni casovi
        if (Boolean.TRUE.equals(onlyCompleted)) {
            Query completedQuery = Query.of(q -> q
                    .term(t -> t
                            .field("completed")
                            .value(true)
                    )
            );
            mustQueries.add(completedQuery);
        }

        // Filter: minimalni skor
        if (minScore != null) {
            Query scoreQuery = Query.of(q -> q
                    .range(RangeQuery.of(r -> r
                            .number(n -> n
                                    .field("score")
                                    .gte(minScore.doubleValue())
                            )
                    ))
            );
            mustQueries.add(scoreQuery);
        }

        // Filter: opseg kilometraze
        if (minKm != null || maxKm != null) {
            Query kmQuery = Query.of(q -> q
                    .range(RangeQuery.of(r -> r
                            .number(n -> {
                                n.field("kmDriven");
                                if (minKm != null) n.gte(minKm.doubleValue());
                                if (maxKm != null) n.lte(maxKm.doubleValue());
                                return n;
                            })
                    ))
            );
            mustQueries.add(kmQuery);
        }

        // Filter: isključi časove sa kvarom na vozilu
        Query noMalfunctionQuery = Query.of(q -> q
                .term(t -> t
                        .field("vehicleInfo.malfunction")
                        .value(false)
                )
        );
        mustQueries.add(noMalfunctionQuery);

        BoolQuery boolQuery = BoolQuery.of(b -> b.must(mustQueries));

        NativeQuery query = new NativeQueryBuilder()
                .withQuery(Query.of(q -> q.bool(boolQuery)))
                .withSort(s -> s.field(f -> f.field("score").order(SortOrder.Desc)))
                .withSort(s -> s.field(f -> f.field("consumedFuelLiters").order(SortOrder.Desc)))
                .withMaxResults(200)
                .build();

        SearchHits<PracticalClassLog> searchHits = elasticsearchOperations.search(
                query,
                PracticalClassLog.class,
                IndexCoordinates.of("practical-class-log")
        );

        List<PracticalClassLog> logs = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        // Agregacija po kategoriji kandidata
        Map<String, Map<String, Object>> statsByCategory = logs.stream()
                .filter(log -> log.getCandidateInfo() != null && log.getCandidateInfo().getCategory() != null)
                .collect(Collectors.groupingBy(
                        log -> log.getCandidateInfo().getCategory(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> {
                                    Map<String, Object> stats = new HashMap<>();
                                    stats.put("numberOfClasses", list.size());
                                    stats.put("averageScore", list.stream()
                                            .mapToInt(PracticalClassLog::getScore)
                                            .average()
                                            .orElse(0.0));
                                    stats.put("totalKmDriven", list.stream()
                                            .mapToInt(l -> l.getKmDriven() != null ? l.getKmDriven() : 0)
                                            .sum());
                                    stats.put("averageFuelConsumption", list.stream()
                                            .mapToDouble(l -> l.getConsumedFuelLiters() != null ? l.getConsumedFuelLiters() : 0.0)
                                            .average()
                                            .orElse(0.0));
                                    return stats;
                                }
                        )
                ));

        // Sortiranje kategorija po prosecnom skoru (desc)
        List<Map.Entry<String, Map<String, Object>>> sortedCategories = statsByCategory.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(
                        (Double) e2.getValue().get("averageScore"),
                        (Double) e1.getValue().get("averageScore")
                ))
                .collect(Collectors.toList());

        // Top 5 časova po skoru
        List<PracticalClassLog> top5 = logs.stream().limit(5).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("totalHits", searchHits.getTotalHits());

        result.put("top5ByScore", top5);
        result.put("statsByCategory", sortedCategories);
        result.put("overallAverageScore", logs.stream()
                .mapToInt(PracticalClassLog::getScore)
                .average()
                .orElse(0.0));
        result.put("logs", logs);

        return result;
    }




//----------------------------------------------------
    public Map<String, Object> findCandidatesForScheduling(
            String category,
            Double minAvgGrade,
            Integer minClasses,
            String prefDate) {

        List<Query> mustQueries = new ArrayList<>();

        // mora
        mustQueries.add(Query.of(q -> q
                .term(t -> t.field("status").value("PRACTICAL"))
        ));
        mustQueries.add(Query.of(q -> q
                .term(t -> t.field("theoryCompleted").value(true))
        ));

        //  filter po kategoriji
        if (category != null && !category.isBlank()) {
            mustQueries.add(Query.of(q -> q
                    .term(t -> t.field("category").value(category))
            ));
        }

        //  filter: prosečna ocena >= minAvgGrade
        if (minAvgGrade != null) {
            mustQueries.add(Query.of(q -> q
                    .range(RangeQuery.of(r -> r
                            .number(n -> n.field("avgClassGrade").gte(minAvgGrade))
                    ))
            ));
        }

        //  filter: broj časova >= minClasses
        if (minClasses != null) {
            mustQueries.add(Query.of(q -> q
                    .range(RangeQuery.of(r -> r
                            .number(n -> n.field("numberOfHeldClasses").gte(minClasses.doubleValue()))
                    ))
            ));
        }

        // filterČ aktivna preferencija za određeni datum
        if (prefDate != null && !prefDate.isBlank()) {
            mustQueries.add(Query.of(q -> q
                    .nested(n -> n
                            .path("activePrefs")
                            .query(nq -> nq.bool(nb -> nb
                                    .must(nf -> nf.term(t -> t
                                            .field("activePrefs.date").value(prefDate)
                                    ))
                            ))
                    )
            ));
        }

        BoolQuery boolQuery = BoolQuery.of(b -> b.must(mustQueries));

        NativeQuery query = new NativeQueryBuilder()
                .withQuery(Query.of(q -> q.bool(boolQuery)))
                .withSort(s -> s.field(f -> f.field("avgClassGrade").order(SortOrder.Desc)))
                .withSort(s -> s.field(f -> f.field("totalKmDriven").order(SortOrder.Desc)))
                .withMaxResults(200)
                .build();

        SearchHits<CandidateAnalytics> searchHits = elasticsearchOperations.search(
                query,
                CandidateAnalytics.class,
                IndexCoordinates.of("candidate-analytics-timeprefs")
        );

        List<CandidateAnalytics> candidates = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        // Agregacija po preferredLocation
        Map<String, Map<String, Object>> statsByLocation = candidates.stream()
                .filter(c -> c.getPreferredLocation() != null)
                .collect(Collectors.groupingBy(
                        CandidateAnalytics::getPreferredLocation,
                        Collectors.collectingAndThen(Collectors.toList(), list -> {
                            Map<String, Object> stats = new HashMap<>();
                            stats.put("candidateCount", list.size());
                            stats.put("averageGrade", list.stream()
                                    .mapToDouble(c -> c.getAvgClassGrade() != null ? c.getAvgClassGrade() : 0.0)
                                    .average().orElse(0.0));
                            stats.put("averageKmDriven", list.stream()
                                    .mapToInt(c -> c.getTotalKmDriven() != null ? c.getTotalKmDriven() : 0)
                                    .average().orElse(0.0));
                            stats.put("totalClasses", list.stream()
                                    .mapToInt(c -> c.getNumberOfHeldClasses() != null ? c.getNumberOfHeldClasses() : 0)
                                    .sum());
                            return stats;
                        })
                ));

        // sortiranje lokacija po broju kandidata DESC
        List<Map.Entry<String, Map<String, Object>>> rankedLocations = statsByLocation.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(
                        (int) e2.getValue().get("candidateCount"),
                        (int) e1.getValue().get("candidateCount")))
                .collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalCandidates", searchHits.getTotalHits());
        result.put("overallAvgGrade", candidates.stream()
                .mapToDouble(c -> c.getAvgClassGrade() != null ? c.getAvgClassGrade() : 0.0)
                .average().orElse(0.0));
        result.put("statsByLocation", rankedLocations);
        result.put("candidates", candidates);

        return result;
    }



//---------------------------


    public Map<String, Object> findProblematicClasses(
            Integer maxScore,
            Integer minKm,
            Long instructorId) {

        List<Query> mustQueries = new ArrayList<>();

        // Uvek: km > 0 (cas je stvarno krenuo)
        mustQueries.add(Query.of(q -> q
                .range(RangeQuery.of(r -> r
                        .number(n -> n.field("kmDriven")
                                .gte(minKm != null ? minKm.doubleValue() : 1.0))
                ))
        ));

        //  filter: score <= maxScore
        if (maxScore != null) {
            mustQueries.add(Query.of(q -> q
                    .range(RangeQuery.of(r -> r
                            .number(n -> n.field("score").lte(maxScore.doubleValue()))
                    ))
            ));
        }

        //  filter: određeni instruktor
        if (instructorId != null) {
            mustQueries.add(Query.of(q -> q
                    .term(t -> t.field("instructorInfo.id").value(instructorId))
            ));
        }


        List<Query> shouldQueries = new ArrayList<>();
        shouldQueries.add(Query.of(q -> q
                .term(t -> t.field("completed").value(false))
        ));
        shouldQueries.add(Query.of(q -> q
                .term(t -> t.field("vehicleInfo.malfunction").value(true))
        ));

        BoolQuery boolQuery = BoolQuery.of(b -> b
                .must(mustQueries)
                .should(shouldQueries)
                .minimumShouldMatch("1")
        );

        NativeQuery query = new NativeQueryBuilder()
                .withQuery(Query.of(q -> q.bool(boolQuery)))
                .withSort(s -> s.field(f -> f.field("score").order(SortOrder.Asc)))
                .withSort(s -> s.field(f -> f.field("startTime").order(SortOrder.Desc)))
                .withMaxResults(200)
                .build();

        SearchHits<PracticalClassLog> searchHits = elasticsearchOperations.search(
                query,
                PracticalClassLog.class,
                IndexCoordinates.of("practical-class-log")
        );

        List<PracticalClassLog> logs = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        // Agregacija po modelu vozila
        Map<String, Map<String, Object>> statsByVehicle = logs.stream()
                .filter(l -> l.getVehicleInfo() != null && l.getVehicleInfo().getModel() != null)
                .collect(Collectors.groupingBy(
                        l -> l.getVehicleInfo().getModel(),
                        Collectors.collectingAndThen(Collectors.toList(), list -> {
                            Map<String, Object> stats = new HashMap<>();
                            stats.put("totalProblematic", list.size());
                            stats.put("malfunctionCount", list.stream()
                                    .filter(l -> l.getVehicleInfo().isMalfunction()).count());
                            stats.put("averageScore", list.stream()
                                    .mapToInt(PracticalClassLog::getScore).average().orElse(0.0));
                            stats.put("notCompletedCount", list.stream()
                                    .filter(l -> !l.isCompleted()).count());
                            return stats;
                        })
                ));

        // Sortiraj modele po broju problematicnih cAsova DESC

        List<Map.Entry<String, Map<String, Object>>> rankedVehicles = statsByVehicle.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(
                        (Integer) e2.getValue().get("totalProblematic"),
                        (Integer) e1.getValue().get("totalProblematic")))
                .collect(Collectors.toList());

        // Top 5 kandidata sa naj problematicnih casova
        Map<Long, Long> problemsPerCandidate = logs.stream()
                .filter(l -> l.getCandidateInfo() != null && l.getCandidateInfo().getId() != null)
                .collect(Collectors.groupingBy(
                        l -> l.getCandidateInfo().getId(),
                        Collectors.counting()
                ));

        List<Map.Entry<Long, Long>> top5Candidates = problemsPerCandidate.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(5)
                .collect(Collectors.toList());

        long totalMalfunctions = logs.stream()
                .filter(l -> l.getVehicleInfo() != null && l.getVehicleInfo().isMalfunction())
                .count();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalProblematic", searchHits.getTotalHits());
        result.put("totalMalfunctions", totalMalfunctions);
        result.put("rankedVehicleModels", rankedVehicles);
        result.put("top5ProblematicCandidates", top5Candidates);
        result.put("logs", logs);

        return result;
    }





}
