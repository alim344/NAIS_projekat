package com.example.class_organization.repo;

import com.example.class_organization.dto.InstNameDTO;
import com.example.class_organization.model.Category;
import com.example.class_organization.model.Instructor;
import com.example.class_organization.model.Teaching;
import com.example.class_organization.model.Vehicle;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InstructorRepository extends Neo4jRepository<Instructor, Long> {


    @Query("MATCH (i:Instructor) WHERE elementId(i) = $id " +
            "DETACH DELETE i " +
            "RETURN count(*)")
    int deleteInstructor(String id);

    //TEACHES

    @Query("MATCH (i:Instructor {username: $username}),(pc:PracticalClass) " +
            "WHERE ID(pc) = $classId " +
            "MERGE (i) -[t:TEACHES] -> (pc) " +
            "SET t.note = $note , t.score = $score " +
            "RETURN count(t)")
    int createTeaches(String username, Long classId, int score, String note);


    @Query("MATCH (i:Instructor {username: $username})-[t:TEACHES]-> (pc:PracticalClass) " +
            "WHERE elementId(pc) = $classId " +
            "SET t.score = $score , t.note = $note " +
            "RETURN count(t)")
    int updateTeaches(String username, String classId, int score, String note);



    @Query("MATCH (:Instructor {username: $username}) -[t:TEACHES] -> (pc: PracticalClass)" +
            " WHERE ID(pc) = $classId " +
            "DELETE t " +
            "RETURN count(*)")
    int deleteTeaches(String username, Long classId);



    @Query("MATCH (:Instructor {username: $username}) -[t:TEACHES] -> (pc: PracticalClass) " +
            " WHERE ID(pc) = $classId " +
            "RETURN t,pc")
    Teaching getTeachingById(String username, Long classId);


    @Query("MATCH (:Instructor {username: $username}) - [t:TEACHES] -> (pc: PracticalClass) " +
            "RETURN t,pc")
    List<Teaching> getAllTeachings(String username);


    //TRAINS

    @Query("MATCH (c:Candidate {username:$candidateUsername}),(i:Instructor {username: $instructorUsername}) " +
            "CREATE (i) - [t:TRAINS] -> (c) " +
            "RETURN count(t)")
    int createTrains(String candidateUsername,String instructorUsername);


    @Query("MATCH (:Instructor {username: $instructorUsername})-[t:TRAINS]->(:Candidate {username: $candidateUsername}) " +
            "DELETE t " +
            "RETURN count(t)")
    int deleteTrains(String candidateUsername, String instructorUsername);




    @Query("MATCH (c:Candidate)-[:HAS_PREFERENCE]->(tp:TimePreference) " +
            "WHERE elementId(c) = $candidateId " +
            "MATCH (otherCandidate:Candidate)-[:HAS_PREFERENCE]->(tp2:TimePreference) " +
            "WHERE otherCandidate.category = c.category " +
            "  AND tp2.date = tp.date " +
            "  AND tp2.startTime <= tp.endTime " +
            "  AND tp2.endTime >= tp.startTime " +
            "  AND elementId(otherCandidate) <> elementId(c) " +
            "MATCH (instructor:Instructor)-[:TRAINS]->(otherCandidate) " +
            "MATCH (instructor)-[teach:TEACHES]->(pc:PracticalClass) " +
            "WITH instructor, AVG(teach.score) AS avgScore, " +
            "     COUNT(DISTINCT otherCandidate) AS sharedCandidates, " +
            "     COUNT { (instructor)-[:TRAINS]->() } AS currentLoad " +
            "WHERE avgScore > 3.5 AND currentLoad < instructor.maxCapacity " +
            "RETURN instructor " +
            "ORDER BY avgScore DESC")
    List<Instructor> recommendInstructorsForCandidate(String candidateId);



    @Query("MATCH (instructor:Instructor)-[teach:TEACHES]->(pc:PracticalClass) " +
            "WHERE pc.completed = true " +
            "WITH instructor, " +
            "     COUNT(pc) AS completedClasses, " +
            "     AVG(teach.score) AS avgScore " +
            "WHERE completedClasses >= $minClasses " +
            "RETURN instructor " +
            "ORDER BY avgScore DESC")
    List<Instructor> findTopInstructorsByScore(int minClasses);

    @Query("MATCH (i:Instructor)-[:TEACHES]->(p:PracticalClass) WHERE p.id = $classId RETURN i")
    Optional<Instructor> findByClassId(@Param("classId") Long classId);

    @Query("MATCH (i:Instructor)-[:DRIVES]->(v:vehicle) WHERE i.id = $instructorId RETURN v")
    Optional<Vehicle> findVehicleByInstructorId(@Param("instructorId") Long instructorId);

    @Query("MATCH (i:Instructor)-[:TEACHES]->(p:PracticalClass) WHERE p.id = $classId RETURN i.id AS id,i.name AS name, i.lastname AS lastname")
    Optional<InstNameDTO> findInstructorNameByClassId(@Param("classId") Long classId);
}
