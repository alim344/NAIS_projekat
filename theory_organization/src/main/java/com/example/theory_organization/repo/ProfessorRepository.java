package com.example.theory_organization.repo;

import com.example.theory_organization.model.Professor;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ProfessorRepository extends Neo4jRepository<Professor, Long> {
    @Query("MATCH (p:Professor) WHERE id(p) = $professorId " +
            "MATCH (tc:TheoryClass) WHERE id(tc) = $theoryClassId " +
            "MERGE (p)-[:LECTURES]->(tc)")
    void createLecturesRelationship(@Param("professorId") Long professorId,
                                    @Param("theoryClassId") Long theoryClassId);

    @Query("MATCH (p:Professor)-[r:LECTURES]->(tc:TheoryClass) " +
            "WHERE id(p) = $professorId AND id(tc) = $theoryClassId " +
            "DELETE r")
    void deleteLecturesRelationship(@Param("professorId") Long professorId, @Param("theoryClassId") Long theoryClassId);

    //KOMPLEKSNI UPIT
    @Query("MATCH (p:Professor)-[:LECTURES]->(tc:TheoryClass) " +
            "WHERE p.academicTitle IS NOT NULL " +
            "WITH p, sum(duration(tc.startTime, tc.endTime).minutes) AS totalMin " +
            "WHERE totalMin > 0 " +
            "RETURN p.name AS name, p.lastname AS lastname, p.academicTitle AS academicTitle, totalMin " +
            "ORDER BY totalMin DESC")
    List<Map<String, Object>> getProfessorStats();
}
