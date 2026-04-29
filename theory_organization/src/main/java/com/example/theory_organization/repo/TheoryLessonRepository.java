package com.example.theory_organization.repo;

import com.example.theory_organization.model.TheoryLesson;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TheoryLessonRepository extends Neo4jRepository<TheoryLesson, Long> {
    List<TheoryLesson> findAllByOrderByOrderNumberAsc();


    //KOMPLEKSNI UPIT
    @Query("MATCH (tl:TheoryLesson) +" +
            "WHERE NOT EXISTS {" +
            "MATCH (c:Candidate {username: $username})-[:ATTENDED_THEORY {completed: true}]->(:TheoryClass)-[:INSTANCE_OF]->(tl)" +
            "}" +
            "WITH tl" +
            "ORDER BY tl.orderNumber ASC" +
            "RETURN min(tl)")
    Optional<TheoryLesson> findFirstMissingLesson(@Param("username") String username);
}
