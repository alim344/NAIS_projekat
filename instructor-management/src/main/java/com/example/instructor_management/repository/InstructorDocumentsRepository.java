package com.example.instructor_management.repository;

import com.example.instructor_management.model.InstructorDocuments;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;
import java.util.Map;

public interface InstructorDocumentsRepository extends Neo4jRepository<InstructorDocuments, String> {

    /*
    @Query("MATCH (d:InstructorDocuments)" +
            "WHERE d.expiryDate <= date() + duration({days:60})" +
            "WITH d.documentType AS type, COUNT(d) AS documentCount" +
            "WHERE type, documentCount" +
            "ORDER BY documentCount DESC")
    List<Map<String, Object>> countExpiringDocumentsByType(); */
}
