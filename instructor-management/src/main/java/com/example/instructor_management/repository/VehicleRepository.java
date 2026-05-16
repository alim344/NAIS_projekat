package com.example.instructor_management.repository;

import com.example.instructor_management.model.Vehicle;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface VehicleRepository extends Neo4jRepository<Vehicle, String> {
}
