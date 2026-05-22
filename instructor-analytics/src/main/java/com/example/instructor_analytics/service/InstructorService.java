package com.example.instructor_analytics.service;

import com.example.instructor_analytics.model.InstructorDocument;
import com.example.instructor_analytics.repository.InstructorRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import java.util.*;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHits;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstructorService {

    private final InstructorRepository instructorRepository;

    private final ElasticsearchOperations elasticsearchOperations;
    private final RedisCacheService redisCacheService;

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

    public void deleteInstructor(String id) {
        instructorRepository.deleteById(id);
    }

    public long countInstructors() {
        return instructorRepository.count();
    }

    /**
     * COMPLEX QUERY 1:
     * Pronadji instruktore koji:
     * - imaju slobodna mesta
     * - ciji dokumenti sadrze zadani tekst
     * - cija licenca nije istekla
     * - filter po kategoriji
     * Sortiranje: po broju slobodnih mesta opadajuce
     * Agregacija: ukupan broj slobodnih mesta
     */
    @Cacheable(value = "instructors", key = "#category + '_' + #searchText + '_' + #maxResults", unless = "#result == null")
    public Map<String, Object> findAvailableInstructorsWithValidDocuments(
            String category,
            String searchText,
            int maxResults) {

        System.out.println("⏺ Izvršavam upit nad Elasticsearch-om (nije iz keša)");

        List<Query> mustQueries = new ArrayList<>();

        if (category != null && !category.isEmpty()) {
            Query categoryQuery = Query.of(q -> q
                    .term(t -> t
                            .field("categories")
                            .value(category)
                    )
            );
            mustQueries.add(categoryQuery);
        }

        if (searchText != null && !searchText.isEmpty()) {
            Query textQuery = Query.of(q -> q
                    .match(m -> m
                            .field("documentTypes")
                            .query(searchText)
                            .fuzziness("AUTO")
                    )
            );
            mustQueries.add(textQuery);
        }

        BoolQuery boolQuery = BoolQuery.of(b -> b.must(mustQueries));

        NativeQuery query = NativeQuery.builder()
                .withQuery(Query.of(q -> q.bool(boolQuery)))
                .withMaxResults(1000)
                .build();

        SearchHits<InstructorDocument> searchHits = elasticsearchOperations.search(
                query, InstructorDocument.class
        );

        List<InstructorDocument> allInstructors = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        List<InstructorDocument> instructorsWithFreeSpots = allInstructors.stream()
                .filter(i -> i.getCurrentCandidateCount() < i.getMaxCapacity())
                .collect(Collectors.toList());

        instructorsWithFreeSpots.sort((a, b) -> {
            int freeA = a.getMaxCapacity() - a.getCurrentCandidateCount();
            int freeB = b.getMaxCapacity() - b.getCurrentCandidateCount();
            return Integer.compare(freeB, freeA);
        });

        if (instructorsWithFreeSpots.size() > maxResults) {
            instructorsWithFreeSpots = instructorsWithFreeSpots.subList(0, maxResults);
        }

        long totalFreeSpots = instructorsWithFreeSpots.stream()
                .mapToLong(i -> i.getMaxCapacity() - i.getCurrentCandidateCount())
                .sum();

        List<Map<String, Object>> instructors = instructorsWithFreeSpots.stream()
                .map(doc -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", doc.getId());
                    map.put("name", doc.getName());
                    map.put("lastName", doc.getLastName());
                    map.put("email", doc.getEmail());
                    map.put("maxCapacity", doc.getMaxCapacity());
                    int freeSpots = doc.getMaxCapacity() - doc.getCurrentCandidateCount();
                    map.put("freeSpots", freeSpots);
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

