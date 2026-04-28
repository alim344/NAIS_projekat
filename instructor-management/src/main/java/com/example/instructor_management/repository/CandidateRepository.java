package repository;

import model.Candidate;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface CandidateRepository extends Neo4jRepository<Candidate, Long> {
}
