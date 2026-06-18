package com.example.instructor_analytics.repository;

import com.example.instructor_analytics.model.VehicleDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleRepository extends ElasticsearchRepository<VehicleDocument, String> {
    Optional<VehicleDocument> findByRegistrationNumber(String registrationNumber);
}
