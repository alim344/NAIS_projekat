package com.example.instructor_analytics.repository;

import com.example.instructor_analytics.model.VehicleDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends ElasticsearchRepository<VehicleDocument, String> {
}
