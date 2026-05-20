package com.example.class_organization.repo;

import com.example.class_organization.model.PracticalClass;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface PracticalClassRepository extends Neo4jRepository<PracticalClass, Long> {

    @Query("MATCH (i:Instructor)-[:TEACHES]->(pc:PracticalClass)<-[:ATTENDS]-(c:Candidate) " +
            "RETURN pc.startTime AS startTime, " +
            "       pc.endTime AS endTime, " +
            "       pc.completed AS completed, " +
            "       c.username AS candidate_username, " +
            "       i.username AS instructor_username")
    List<Map<String, Object>> getAllPracticalClasses();




    @Query("MATCH (pc:PracticalClass) WHERE elementId(pc) = $id " +
            "DETACH DELETE pc " +
            "RETURN count(*)")
    int deleteClassAndAllConnections(String id);


    @Query("MATCH (pc: PracticalClass) WHERE elementId(pc) = $id" +
            " SET pc.completed = $completed " +
            " RETURN pc")
    PracticalClass updateCompleted(String id, Boolean completed);

}
