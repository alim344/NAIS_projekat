package com.example.class_organization.repo;

import com.example.class_organization.model.Instructor;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

public interface InstructorRepository extends Neo4jRepository<Instructor, Long> {


    @Query("MATCH (i:Instructor {username: $username}),(pc:PracticalClass) " +
            "WHERE ID(pc) = $classId " +
            "MERGE (i) -[t:TEACHES] -> (pc) " +
            "SET t.note = $note , t.score = $score " +
            "RETURN count(t)")
    int createTeaches(String username, Long classId, int score, String note);

}
