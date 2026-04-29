package com.example.theory_organization.repo;

import com.example.theory_organization.model.Classroom;
import com.example.theory_organization.model.TheoryClass;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TheoryClassRepository extends Neo4jRepository<TheoryClass, Long> {

    @Query("MATCH (tc : TheoryClass), (cr: Classroom)" +
           "WHERE elementId(tc) = $theoryClassId AND elementId(cr) = $classroomId" +
            "MERGE (tc)-[:HELD_IN]->(cr)")
    void createHeldInRelationship(@Param("theoryClassId") Long theoryClassId, @Param("classroomId") Long classroomId);

    @Query("MATCH (tc:TheoryClass)-[r:HELD_IN]->(c:Classroom) " +
            "WHERE id(tc) = $theoryClassId " +
            "DELETE r")
    void deleteHeldInRelationship(@Param("theoryClassId") Long theoryClassId);

    @Query("MATCH (tc:TheoryClass), (tl:TheoryLesson) " +
            "WHERE id(tc) = $theoryClassId AND id(tl) = $theoryLessonId " +
            "MERGE (tc)-[:INSTANCE_OF]->(tl)")
    void createInstanceOfRelationship(@Param("theoryClassId") Long theoryClassId, @Param("theoryLessonId") Long theoryLessonId);

    @Query("MATCH (tc:TheoryClass)-[r:INSTANCE_OF]->(tl:TheoryLesson) " +
            "WHERE id(tc) = $theoryClassId " +
            "DELETE r")
    void deleteInstanceOfRelationship(@Param("theoryClassId") Long theoryClassId);

    @Query("MATCH (tc:TheoryClass)-[:HELD_IN]->(cr:Classroom) " +
            "OPTIONAL MATCH (cand:Candidate)-[:ATTENDED_THEORY]->(tc) " +
            "WITH tc, cr, count(cand) AS candidateCount " +
            "WHERE candidateCount > cr.capacity " +
            "RETURN tc")
    List<TheoryClass> findOverbookedClasses();

    @Query("MATCH (cr:Classroom) " +
            "OPTIONAL MATCH (tc:TheoryClass)-[:HELD_IN]->(cr) " +
            "WHERE tc.startTime <= $targetTime AND tc.endTime > $targetTime " +
            "WITH cr, count(tc) AS conflictCount " +
            "WHERE conflictCount = 0 " +
            "RETURN cr")
    List<Classroom> findAvailableClassrooms(@Param("targetTime") LocalDateTime targetTime);
}
