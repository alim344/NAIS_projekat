package com.example.instructor_analytics.service;

import com.example.instructor_analytics.model.InstructorDocument;
import com.example.instructor_analytics.repository.InstructorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InstructorService {

    private final InstructorRepository instructorRepository;
    private final ElasticsearchOperations elasticsearchOperations;

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

    public Map<String, Object> getInstructorsWithAvailableSpotsByCategory(String category) {
        Criteria criteria = new Criteria("categories").is(category);
        CriteriaQuery query = new CriteriaQuery(criteria);
        query.addSort(Sort.by(Sort.Direction.DESC, "maxCapacity"));

        SearchHits<InstructorDocument> hits = elasticsearchOperations.search(query, InstructorDocument.class);

        long totalAvailableSpots = hits.getSearchHits().stream()
                .mapToLong(h -> h.getContent().getMaxCapacity() - h.getContent().getCurrentCandidateCount())
                .sum();

        List<InstructorDocument> instructors = hits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("category", category);
        result.put("totalAvailableSpots", totalAvailableSpots);
        result.put("instructorCount", instructors.size());
        result.put("instructors", instructors);

        return result;
    }
}
