package com.example.instructor_analytics.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import com.example.instructor_analytics.model.InstructorDocument;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructorRepository extends ElasticsearchRepository<InstructorDocument, String> {
}
