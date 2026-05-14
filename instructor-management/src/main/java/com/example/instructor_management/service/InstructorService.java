package com.example.instructor_management.service;

import com.example.instructor_management.model.Instructor;
import com.example.instructor_management.repository.CandidateRepository;
import com.example.instructor_management.repository.InstructorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InstructorService {

    private final InstructorRepository instructorRepository;
    private final CandidateRepository candidateRepository;

    public List<Instructor> getAllInstructors() {
        return instructorRepository.findAll();
    }

    public Instructor getInstructorById(String id) {
        return instructorRepository.findById(id).orElse(null);
    }

    public Instructor saveInstructor(Instructor instructor) {
        return instructorRepository.save(instructor);
    }

    public void deleteInstructor(String id) {
        instructorRepository.deleteById(id);
    }

    @Transactional
    public void assignVehicleToInstructor(String instructorId, String vehicleId, Integer mileageAtAssignment) {
        instructorRepository.assignVehicleToInstructor(
                instructorId,
                vehicleId,
                LocalDate.now(),
                mileageAtAssignment
        );
    }
    @Transactional
    public void assignCandidateToInstructor(String instructorId, String candidateId, String status) {
        instructorRepository.assignCandidateToInstructor(instructorId, candidateId, status);
    }

  private final Neo4jClient neo4jClient;

    public List<Map<String, Object>> getInstructorsWithCandidateCount() {
        String query = "MATCH (i:Instructor)-[:ASSIGNED_TO]->(c:Candidate) " +
                "RETURN elementId(i) AS id, i.name AS name, i.lastname AS lastname, COUNT(c) AS candidateCount " +
                "ORDER BY candidateCount DESC";

        Collection<Map<String, Object>> results = neo4jClient.query(query)
                .fetch()
                .all();

        return new ArrayList<>(results);
    }


}
