package com.example.class_organization.repo;

import com.example.class_organization.model.Instructor;
import com.example.class_organization.model.Teaching;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;

public interface InstructorRepository extends Neo4jRepository<Instructor, Long> {


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


    @Query("MATCH (:Candidate {username:$candidateUsername}) -[t:TEACHES]->(:Instructor {username: $instructorUsername}) " +
            "DELETE t " +
            "RETURN count(*)")
    int deleteTrains(String candidateUsername,String instructorUsername);

}
