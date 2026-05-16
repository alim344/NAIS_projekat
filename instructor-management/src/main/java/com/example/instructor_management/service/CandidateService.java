package com.example.instructor_management.service;

import com.example.instructor_management.DTO.CandidateDTO;
import com.example.instructor_management.model.Candidate;
import com.example.instructor_management.repository.CandidateRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Service
public class CandidateService {

    private final CandidateRepository candidateRepository;

    public Candidate createCandidate(Candidate candidate) {
        return candidateRepository.save(candidate);
    }

    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }

    public Candidate getCandidateById(String id) {
        return candidateRepository.findById(id).orElse(null);
    }

    public Candidate updateCandidate(String id, CandidateDTO updateDTO) {
        Candidate existing = candidateRepository.findById(id).orElse(null);
        if (existing == null) return null;

        existing.setUsername(updateDTO.getUsername());
        existing.setPassword(updateDTO.getPassword());
        existing.setEmail(updateDTO.getEmail());
        existing.setName(updateDTO.getName());
        existing.setLastname(updateDTO.getLastname());
        existing.setTheoryCompleted(updateDTO.isTheoryCompleted());
        existing.setCategory(updateDTO.getCategory());
        existing.setTrainingStatus(updateDTO.getTrainingStatus());

        return candidateRepository.save(existing);
    }

    public void deleteCandidate(String id) {
        candidateRepository.deleteById(id);
    }

    public Candidate convertToEntity(CandidateDTO dto) {
        Candidate candidate = new Candidate();
        candidate.setId(dto.getId());
        candidate.setUsername(dto.getUsername());
        candidate.setPassword(dto.getPassword());
        candidate.setEmail(dto.getEmail());
        candidate.setName(dto.getName());
        candidate.setLastname(dto.getLastname());
        candidate.setTheoryCompleted(dto.isTheoryCompleted());
        candidate.setCategory(dto.getCategory());
        candidate.setTrainingStatus(dto.getTrainingStatus());
        return candidate;
    }

    public CandidateDTO toDTO(Candidate candidate) {
        return new CandidateDTO(
                candidate.getId(),
                candidate.getUsername(),
                candidate.getPassword(),
                candidate.getEmail(),
                candidate.getName(),
                candidate.getLastname(),
                candidate.isTheoryCompleted(),
                candidate.getCategory(),
                candidate.getTrainingStatus()
        );
    }

    private final Neo4jClient neo4jClient;

    public List<Map<String, Object>> getUnassignedPracticalCandidatesWithClient() {
        String query = "MATCH (c:Candidate) " +
                "WHERE c.trainingStatus = 'PRACTICAL' " +
                "OPTIONAL MATCH (i:Instructor)-[:ASSIGNED_TO]->(c) " +
                "WITH c, i " +
                "WHERE i IS NULL " +
                "RETURN elementId(c) AS id, c.name AS name, c.lastname AS lastname, c.trainingStatus AS trainingStatus";

        return new ArrayList<>(neo4jClient.query(query).fetch().all());
    }
}
