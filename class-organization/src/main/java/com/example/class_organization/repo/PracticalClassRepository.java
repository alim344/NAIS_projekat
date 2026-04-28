package com.example.class_organization.repo;

import com.example.class_organization.model.PracticalClass;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface PracticalClassRepository extends Neo4jRepository<PracticalClass, Long> {
}
