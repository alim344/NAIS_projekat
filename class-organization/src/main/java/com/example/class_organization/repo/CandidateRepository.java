package com.example.class_organization.repo;

import com.example.class_organization.model.Candidate;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

public interface CandidateRepository extends Neo4jRepository<Candidate, Long> {


    @Query("MATCH (c:Candidate {username: $username}) - [a:ATTENDS] -> (pc: PracticalClass) " +
            "WHERE ID(pc) = $classId " +
            "SET a.kmDriven = $km, a.note = $note " +
            "RETURN count(a)")
    int updateAttendanceById(String username,Long classId,int km , String note);


   /* @Query("MATCH (c:Candidate {username: $username}), " +
            "(pc:PracticalClass) WHERE ID(pc) = $classId " +
            "CREATE (c) - [:ATTENDS {kmDriven: $km, note: $note}]->(pc)" +
            " RETURN count(a)")
    int createAttendance(String username,Long classId,int km , String note);*/

    @Query("MATCH (c:Candidate {username: $username}), (pc:PracticalClass) " +
            "WHERE ID(pc) = $classId " +
            "MERGE (c)-[a:ATTENDS]->(pc) " +
            "SET a.kmDriven = $km, a.note = $note " +
            "RETURN count(a)")
    int createAttendance(String username, Long classId, int km, String note);

}
