package repository;

import model.Instructor;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface InstructorRepository extends Neo4jRepository<Instructor, Long> {
}
