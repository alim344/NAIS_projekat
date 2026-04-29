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
    @Query("MATCH (p : Professor), (tc : TheoryClass)" +
            "WHERE elementId(p) = $professorId AND elementId(tc) = $TheoryClass " +
            "MERGE (p)-[:LECTURES]->(tc)")
    void createLecturesRelationship(@Param("professorId") Long professorId, @Param("TheoryClassId") Long theoryClassId);

    @Query("MATCH (p:Professor)-[r:LECTURES]->(tc:TheoryClass)" +
            "WHERE elementId($professorId)= $professorId AND elementId(tc) = $theoryClassId" +
            "DELETE r")
    void deleteLecturesRelationship(@Param("professorId") Long professorId, @Param("theoryClassId") Long theoryClassId);


    //KOMPLEKSNI UPIT
    @Query("MATCH (p:Professor)-[:LECTURES]->(tc:TheoryClass)" +
            "WHERE p.academicTitle IS NOT NULL" +
            "WITH p, sum(duration.inMinutes(datetime(tc.startTime), datetime(tc.endTime))) AS totalMin" +
            "RETURN p.name AS name, totalMin")
    List<Map<String, Object>> getProfessorStats();
}
