package com.example.theory_organization.repo;

import com.example.theory_organization.model.Classroom;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassroomRepository extends Neo4jRepository<Classroom, Long> {
}
