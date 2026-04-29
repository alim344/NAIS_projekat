package com.example.instructor_management.repository;


import com.example.instructor_management.model.Instructor;
import jakarta.transaction.Transactional;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface InstructorRepository extends Neo4jRepository<Instructor, String> {

    @Query("MATCH (i:Instructor) WHERE i.maxCapacity > $capacity RETURN i")
    List<Instructor> findByMaxCapacityGreaterThan(Integer capacity);


   /* @Query("MATCH (i:Instructor)-[:ASSIGNED_TO]->(c:Candidate) " +
            "RETURN elementId(i) AS id, i.name AS name, i.lastname AS lastname, COUNT(c) AS candidateCount " +
            "ORDER BY candidateCount DESC")
    List<Map<String, Object>> findAllInstructorsWithCandidateCount(); */

    @Transactional
    @Query("MATCH (i:User), (v:Vehicle) " +
            "WHERE elementId(i) = $instructorId AND elementId(v) = $vehicleId " +
            "CREATE (i)-[:DRIVES]->(v)")
    void assignVehicleToInstructor(@Param("instructorId") String instructorId,
                                   @Param("vehicleId") String vehicleId);


    @Transactional
    @Query("MATCH (i:Instructor), (c:Candidate) " +
            "WHERE elementId(i) = $instructorId AND elementId(c) = $candidateId " +
            "CREATE (i)-[:ASSIGNED_TO {assignedDate: date(), status: $status}]->(c)")
    void assignCandidateToInstructor(@Param("instructorId") String instructorId,
                                     @Param("candidateId") String candidateId,
                                     @Param("status") String status);

}
