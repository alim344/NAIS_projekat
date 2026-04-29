package com.example.class_organization.repo;

import com.example.class_organization.model.Attendance;
import com.example.class_organization.model.Candidate;
import com.example.class_organization.model.TrainingStatus;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;
import java.util.Optional;

public interface CandidateRepository extends Neo4jRepository<Candidate, Long> {


    @Query("MATCH (c:Candidate) WHERE elementId(c) = $id " +
            "DETACH DELETE c " +
            "RETURN count(*)")
    int deleteCandidate(String id);


    @Query("MATCH (c:Candidate) WHERE elementId(c) = $id " +
            "SET c.status = $status " +
            "RETURN c")
    Candidate updateStatus(String id, TrainingStatus status);

    @Query("MATCH (c:Candidate) WHERE elementId(c) = $id " +
            "SET c.preferredLocation = $prefLocation " +
            "RETURN c")
    Candidate updatePrefLocation(String id, String prefLocation);


    //ATTENDS

    @Query("MATCH (c:Candidate {username: $username}) - [a:ATTENDS] -> (pc: PracticalClass) " +
            "WHERE elementId(pc) = $classId " +
            "SET a.kmDriven = $km, a.present = $present " +
            "RETURN count(a)")
    int updateAttendanceById(String username,String classId,int km , boolean present);


   /* @Query("MATCH (c:Candidate {username: $username}), " +
            "(pc:PracticalClass) WHERE ID(pc) = $classId " +
            "CREATE (c) - [:ATTENDS {kmDriven: $km, note: $note}]->(pc)" +
            " RETURN count(a)")
    int createAttendance(String username,Long classId,int km , String note);*/

    @Query("MATCH (c:Candidate {username: $username}), (pc:PracticalClass) " +
            "WHERE ID(pc) = $classId " +
            "MERGE (c)-[a:ATTENDS]->(pc) " +
            "SET a.kmDriven = $km, a.present = $present " +
            "RETURN count(a)")
    int createAttendance(String username, Long classId, int km, boolean present);


    @Query("MATCH (c:Candidate {username: $username})-[a:ATTENDS]->(pc:PracticalClass) " +
            "WHERE ID(pc) = $classId " +
            "DELETE a " +
            "RETURN count(*)")
    int deleteAttendanceById(String username,Long classId);


    @Query("MATCH (:Candidate {username: $username})-[a:ATTENDS]->(pc:PracticalClass) " +
            "WHERE ID(pc) = $classId " +
            "RETURN a,pc")
    Attendance findAttendanceById(String username,Long classId);

    /*@Query("MATCH (c:Candidate {username: $username})-[a:ATTENDS]->(pc:PracticalClass) " +
            "RETURN a,pc")
    List<Attendance> findAllAttendancesByUsername(String username);*/

    @Query("MATCH (c:Candidate {username: $username})-[a:ATTENDS]->(pc:PracticalClass) " +
            "RETURN a, pc")
    List<Attendance> findAllAttendancesByUsername(String username);

    @Query("MATCH (c:Candidate {username: $username})-[a:ATTENDS]->(pc:PracticalClass) " +
            "RETURN c, collect(a), collect(pc)")
    Optional<Candidate> findByUsernameWithAttendances(String username);


    //HAS_PREFERENCE

    @Query("MATCH (c:Candidate {username: $username}),(tp:TimePreference)" +
            " WHERE ID(tp) = $timeprefId " +
            "CREATE (c) - [h:HAS_PREFERENCE] -> (tp)" +
            " RETURN count(h)")
    int createHasPreference(String username, Long timeprefId);


    @Query("MATCH (:Candidate{username:$username}) - [h:HAS_PREFERENCE] -> (tp:TimePreference) " +
            "WHERE ID(tp) = $timeprefId " +
            "DELETE h" +
            " RETURN count(*)")
    int deleteHasPreference(String username, Long timeprefId);


}
