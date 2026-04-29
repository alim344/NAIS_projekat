package com.example.instructor_management.repository;

import com.example.instructor_management.model.Candidate;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;

public interface CandidateRepository extends Neo4jRepository<Candidate, String> {

    @Query("MATCH (c:Candiate) WHERE c.theoryCompleted = true RETURN c")
    List<Candidate> findAllWhoCompletedTheory();

    /*
    @Query("MATCH (c:Candidate)" +
            "WHERE c.trainingStatus = 'PRACTICAL' " +
            "OPTIONAL MATCH (i:Instructor)-[:ASSIGNED_TO]->(c)" +
            "WITH c,i" +
            "WHERE i IS NULL" +
            "WITH COLLECT(c) as unassignedCandidates "+
            "RETURN unassignedCandidates, SIZE(unassignedCandidates) as totalCount")
    List<Candidate> findUnassignedPracticalCandidates(); */
}
