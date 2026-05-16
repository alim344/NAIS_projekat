package com.example.theory_organization.repo;

import com.example.theory_organization.model.Candidate;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface CandidateRepository extends Neo4jRepository<Candidate, Long> {
    Candidate getCandidateById(Long id);
    Optional<Candidate> findByEmail(String username);
    Optional<Candidate> findByUsername(String username);

    @Query("MATCH (c:Candidate), (tc:TheoryClass) " +
            "WHERE id(c) = $candidateId AND id(tc) = $theoryClassId " +
            "MERGE (c)-[r:ATTENDED_THEORY]->(tc) " +
            "ON CREATE SET r.completed = false")
    void enrollCandidateInTheory(@Param("candidateId") Long candidateId, @Param("theoryClassId") Long theoryClassId);

    @Query("MATCH (c:Candidate)-[r:ATTENDED_THEORY]->(tc:TheoryClass) " +
            "WHERE id(c) = $candidateId AND id(tc) = $theoryClassId " +
            "SET r.completed = true, r.arrivalTime = localdatetime() " +
            "RETURN count(r) > 0")
    Optional<Boolean> makeTheoryAttendanceCompleted(@Param("candidateId") Long candidateId, @Param("theoryClassId") Long theoryClassId);

    @Query("MATCH (c:Candidate)-[r:ATTENDED_THEORY]->(tc:TheoryClass) " +
            "WHERE id(c) = $candidateId AND id(tc) = $theoryClassId " +
            "DELETE r")
    void deleteAttendedRelationship(@Param("candidateId") Long candidateId, @Param("theoryClassId") Long theoryClassId);

}
