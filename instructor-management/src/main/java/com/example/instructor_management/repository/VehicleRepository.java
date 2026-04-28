package repository;

import model.Vehicle;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface VehicleRepository extends Neo4jRepository<Vehicle, Long> {
}
