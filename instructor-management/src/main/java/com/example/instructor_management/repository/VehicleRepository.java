package com.example.instructor_management.repository;

import com.example.instructor_management.model.Vehicle;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

public interface VehicleRepository extends Neo4jRepository<Vehicle, String> {
    Optional<Vehicle> findByRegistrationNumber(String registrationNumber);
}
