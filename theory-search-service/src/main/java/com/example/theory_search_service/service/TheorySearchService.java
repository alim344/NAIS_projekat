package com.example.theory_search_service.service;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.json.JsonData;
import com.example.theory_search_service.model.TheoryClassLog;
import com.example.theory_search_service.model.TheoryQuestion;
import lombok.RequiredArgsConstructor;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TheorySearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    public Map<String, Object> searchQuestionsWithStats(
            String searchText,
            Integer lessonOrderNumber,
            Integer minDifficulty,
            Integer maxDifficulty,
            String category) {

        List<Query> mustQueries = new ArrayList<>();

        if (searchText != null && !searchText.isBlank()) {
            mustQueries.add(Query.of(q -> q
                    .multiMatch(m -> m
                            .query(searchText)
                            .fields("questionText", "lessonTitle")
                            .fuzziness("AUTO")
                    )
            ));
        }

        if (lessonOrderNumber != null) {
            mustQueries.add(Query.of(q -> q
                    .term(t -> t.field("lessonOrderNumber").value(lessonOrderNumber))
            ));
        }

        if (minDifficulty != null || maxDifficulty != null) {
            mustQueries.add(Query.of(q -> q
                    .range(r -> {
                        r.field("difficultyLevel");
                        if (minDifficulty != null) r.gte(JsonData.of(minDifficulty));
                        if (maxDifficulty != null) r.lte(JsonData.of(maxDifficulty));
                        return r;
                    })
            ));
        }

        if (category != null && !category.isBlank()) {
            mustQueries.add(Query.of(q -> q
                    .term(t -> t.field("category").value(category))
            ));
        }

        BoolQuery boolQuery = BoolQuery.of(b -> b.must(mustQueries));

        NativeQuery query = new NativeQueryBuilder()
                .withQuery(Query.of(q -> q.bool(boolQuery)))
                .withSort(s -> s.field(f -> f.field("difficultyLevel").order(SortOrder.Asc)))
                .withSort(s -> s.field(f -> f.field("lessonOrderNumber").order(SortOrder.Asc)))
                .withMaxResults(200)
                .build();

        SearchHits<TheoryQuestion> searchHits = elasticsearchOperations.search(
                query, TheoryQuestion.class, IndexCoordinates.of("theory-questions"));

        List<TheoryQuestion> questions = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent).collect(Collectors.toList());

        Map<String, Map<String, Object>> statsByCategory = questions.stream()
                .filter(q -> q.getCategory() != null)
                .collect(Collectors.groupingBy(
                        TheoryQuestion::getCategory,
                        Collectors.collectingAndThen(Collectors.toList(), list -> {
                            Map<String, Object> stats = new HashMap<>();
                            stats.put("count", list.size());
                            stats.put("averageDifficulty", list.stream()
                                    .mapToInt(TheoryQuestion::getDifficultyLevel).average().orElse(0.0));
                            stats.put("minDifficulty", list.stream()
                                    .mapToInt(TheoryQuestion::getDifficultyLevel).min().orElse(0));
                            stats.put("maxDifficulty", list.stream()
                                    .mapToInt(TheoryQuestion::getDifficultyLevel).max().orElse(0));
                            stats.put("lessons", list.stream()
                                    .map(TheoryQuestion::getLessonTitle).distinct()
                                    .collect(Collectors.toList()));
                            return stats;
                        })
                ));

        List<Map.Entry<String, Map<String, Object>>> sortedCategories = statsByCategory.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(
                        (Integer) e2.getValue().get("count"),
                        (Integer) e1.getValue().get("count")))
                .collect(Collectors.toList());

        List<TheoryQuestion> top5Hardest = questions.stream()
                .sorted(Comparator.comparingInt(TheoryQuestion::getDifficultyLevel).reversed())
                .limit(5).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("totalHits", searchHits.getTotalHits());
        result.put("questions", questions);
        result.put("top5Hardest", top5Hardest);
        result.put("statsByCategory", sortedCategories);
        result.put("overallAverageDifficulty", questions.stream()
                .mapToInt(TheoryQuestion::getDifficultyLevel).average().orElse(0.0));
        return result;
    }

    public Map<String, Object> analyzeClassesByDateAndProfessor(
            LocalDateTime fromDate,
            LocalDateTime toDate,
            String professorUsername,
            Integer minCandidates,
            Boolean onlyFullyAttended) {

        List<Query> mustQueries = new ArrayList<>();

        if (fromDate != null && toDate != null) {
            mustQueries.add(Query.of(q -> q
                    .range(r -> r
                            .field("startTime")
                            .gte(JsonData.of(fromDate.format(formatter)))
                            .lte(JsonData.of(toDate.format(formatter)))
                    )
            ));
        }

        if (professorUsername != null && !professorUsername.isBlank()) {
            mustQueries.add(Query.of(q -> q
                    .term(t -> t.field("professorUsername").value(professorUsername))
            ));
        }

        if (minCandidates != null) {
            mustQueries.add(Query.of(q -> q
                    .range(r -> r
                            .field("candidateCount")
                            .gte(JsonData.of(minCandidates))
                    )
            ));
        }

        if (Boolean.TRUE.equals(onlyFullyAttended)) {
            mustQueries.add(Query.of(q -> q
                    .term(t -> t.field("fullyAttended").value(true))
            ));
        }

        BoolQuery boolQuery = BoolQuery.of(b -> b.must(mustQueries));

        NativeQuery query = new NativeQueryBuilder()
                .withQuery(Query.of(q -> q.bool(boolQuery)))
                .withSort(s -> s.field(f -> f.field("startTime").order(SortOrder.Desc)))
                .withSort(s -> s.field(f -> f.field("candidateCount").order(SortOrder.Desc)))
                .withMaxResults(500)
                .build();

        SearchHits<TheoryClassLog> searchHits = elasticsearchOperations.search(
                query, TheoryClassLog.class, IndexCoordinates.of("theory-class-log"));

        List<TheoryClassLog> classes = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent).collect(Collectors.toList());

        Map<String, Map<String, Object>> statsByProfessor = classes.stream()
                .filter(c -> c.getProfessorUsername() != null)
                .collect(Collectors.groupingBy(
                        TheoryClassLog::getProfessorUsername,
                        Collectors.collectingAndThen(Collectors.toList(), list -> {
                            Map<String, Object> stats = new HashMap<>();
                            stats.put("fullName", list.get(0).getProfessorFullName());
                            stats.put("classCount", list.size());
                            stats.put("totalCandidates", list.stream()
                                    .mapToInt(TheoryClassLog::getCandidateCount).sum());
                            stats.put("averageCandidatesPerClass", list.stream()
                                    .mapToInt(TheoryClassLog::getCandidateCount).average().orElse(0.0));
                            stats.put("averageDurationMinutes", list.stream()
                                    .mapToInt(TheoryClassLog::getDurationMinutes).average().orElse(0.0));
                            stats.put("totalDurationMinutes", list.stream()
                                    .mapToInt(TheoryClassLog::getDurationMinutes).sum());
                            stats.put("averageScore", list.stream()
                                    .mapToDouble(TheoryClassLog::getAverageCandidateScore).average().orElse(0.0));
                            return stats;
                        })
                ));

        List<Map.Entry<String, Map<String, Object>>> sortedProfessors = statsByProfessor.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(
                        (Integer) e2.getValue().get("classCount"),
                        (Integer) e1.getValue().get("classCount")))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("totalHits", searchHits.getTotalHits());
        result.put("classes", classes);
        result.put("statsByProfessor", sortedProfessors);
        result.put("overallAverageDuration", classes.stream()
                .mapToInt(TheoryClassLog::getDurationMinutes).average().orElse(0.0));
        result.put("overallAverageScore", classes.stream()
                .mapToDouble(TheoryClassLog::getAverageCandidateScore).average().orElse(0.0));
        result.put("totalCandidatesAttended", classes.stream()
                .mapToInt(TheoryClassLog::getCandidateCount).sum());
        return result;
    }

    public Map<String, Object> analyzeClassroomUsage(
            String classroomName,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            Integer minDurationMinutes) {

        List<Query> mustQueries = new ArrayList<>();

        if (classroomName != null && !classroomName.isBlank()) {
            mustQueries.add(Query.of(q -> q
                    .multiMatch(m -> m
                            .query(classroomName)
                            .fields("classroomName")
                            .fuzziness("AUTO")
                    )
            ));
        }

        if (fromDate != null && toDate != null) {
            mustQueries.add(Query.of(q -> q
                    .range(r -> r
                            .field("startTime")
                            .gte(JsonData.of(fromDate.format(formatter)))
                            .lte(JsonData.of(toDate.format(formatter)))
                    )
            ));
        }

        if (minDurationMinutes != null) {
            mustQueries.add(Query.of(q -> q
                    .range(r -> r
                            .field("durationMinutes")
                            .gte(JsonData.of(minDurationMinutes))
                    )
            ));
        }

        mustQueries.add(Query.of(q -> q.exists(e -> e.field("classroomName"))));

        BoolQuery boolQuery = BoolQuery.of(b -> b.must(mustQueries));

        NativeQuery query = new NativeQueryBuilder()
                .withQuery(Query.of(q -> q.bool(boolQuery)))
                .withSort(s -> s.field(f -> f.field("durationMinutes").order(SortOrder.Desc)))
                .withSort(s -> s.field(f -> f.field("startTime").order(SortOrder.Asc)))
                .withMaxResults(500)
                .build();

        SearchHits<TheoryClassLog> searchHits = elasticsearchOperations.search(
                query, TheoryClassLog.class, IndexCoordinates.of("theory-class-log"));

        List<TheoryClassLog> classes = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent).collect(Collectors.toList());

        Map<String, Map<String, Object>> statsByClassroom = classes.stream()
                .filter(c -> c.getClassroomName() != null)
                .collect(Collectors.groupingBy(
                        TheoryClassLog::getClassroomName,
                        Collectors.collectingAndThen(Collectors.toList(), list -> {
                            Map<String, Object> stats = new HashMap<>();
                            stats.put("totalClasses", list.size());
                            stats.put("capacity", list.get(0).getClassroomCapacity());
                            stats.put("totalDurationMinutes", list.stream()
                                    .mapToInt(TheoryClassLog::getDurationMinutes).sum());
                            stats.put("averageDurationMinutes", list.stream()
                                    .mapToInt(TheoryClassLog::getDurationMinutes).average().orElse(0.0));
                            stats.put("averageCandidatesPerClass", list.stream()
                                    .mapToInt(TheoryClassLog::getCandidateCount).average().orElse(0.0));
                            stats.put("maxCandidatesInClass", list.stream()
                                    .mapToInt(TheoryClassLog::getCandidateCount).max().orElse(0));
                            stats.put("averageOccupancyPercent", list.stream()
                                    .mapToDouble(c -> c.getClassroomCapacity() > 0
                                            ? (double) c.getCandidateCount() / c.getClassroomCapacity() * 100
                                            : 0.0)
                                    .average().orElse(0.0));
                            stats.put("lessonsHeld", list.stream()
                                    .map(TheoryClassLog::getLessonTitle).distinct()
                                    .collect(Collectors.toList()));
                            return stats;
                        })
                ));

        List<Map.Entry<String, Map<String, Object>>> sortedClassrooms = statsByClassroom.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(
                        (Integer) e2.getValue().get("totalClasses"),
                        (Integer) e1.getValue().get("totalClasses")))
                .collect(Collectors.toList());

        List<Map.Entry<String, Map<String, Object>>> mostOccupied = statsByClassroom.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(
                        (Double) e2.getValue().get("averageOccupancyPercent"),
                        (Double) e1.getValue().get("averageOccupancyPercent")))
                .limit(3).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("totalHits", searchHits.getTotalHits());
        result.put("classes", classes);
        result.put("statsByClassroom", sortedClassrooms);
        result.put("top3MostOccupied", mostOccupied);
        result.put("overallAverageDuration", classes.stream()
                .mapToInt(TheoryClassLog::getDurationMinutes).average().orElse(0.0));
        return result;
    }
}
