package com.example.instructor_management.controller;


import com.example.instructor_management.DTO.CandidateDTO;
import com.example.instructor_management.model.Candidate;
import com.example.instructor_management.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/candidates")
@RequiredArgsConstructor
public class CandidateController {
    private final CandidateService candidateService;

    @GetMapping
    public List<CandidateDTO> getAllCandidates() {
        return candidateService.getAllCandidates().stream()
                .map(candidateService::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CandidateDTO> getCandidateById(@PathVariable String id) {
        Candidate candidate = candidateService.getCandidateById(id);
        return candidate != null
                ? ResponseEntity.ok(candidateService.toDTO(candidate))
                : ResponseEntity.notFound().build();
    }

    @PostMapping
    public CandidateDTO createCandidate(@RequestBody CandidateDTO createDTO) {
        Candidate candidate = candidateService.convertToEntity(createDTO);
        return candidateService.toDTO(candidateService.createCandidate(candidate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandidate(@PathVariable String id) {
        candidateService.deleteCandidate(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CandidateDTO> updateCandidate(
            @PathVariable String id,
            @RequestBody CandidateDTO updateDTO) {
        Candidate updatedCandidate = candidateService.updateCandidate(id, updateDTO);
        if (updatedCandidate == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(candidateService.toDTO(updatedCandidate));
    }

    @GetMapping("/unassigned-practical")
    public List<Map<String, Object>> getUnassignedPracticalCandidates() {
        return candidateService.getUnassignedPracticalCandidatesWithClient();
    }


}
