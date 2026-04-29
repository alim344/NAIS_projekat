package com.example.theory_organization.service;


import com.example.theory_organization.dto.RegistrationDTO;
import com.example.theory_organization.model.Candidate;
import com.example.theory_organization.model.TheoryClass;
import com.example.theory_organization.repo.CandidateRepository;
import com.example.theory_organization.repo.TheoryClassRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CandidateService {
    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private final TheoryClassRepository theoryClassRepository;

    public Candidate registerCandidate(RegistrationDTO dto){
        Candidate candidate = new Candidate();
        candidate.setName(dto.getName());
        candidate.setEmail(dto.getEmail());
        candidate.setPassword(dto.getPassword());
        candidate.setUsername(dto.getUsername());
        candidate.setLastname(dto.getLastname());
        candidate.setCategory(dto.getCategory());
        candidate.setStatus(dto.getStatus());
        candidate.setPreferredLocation(dto.getPreferredLocation());
        candidate.setTheoryCompleted(dto.isTheoryCompleted());
        candidate.setStartOfTraining(dto.getStartOfTraining());
        return candidateRepository.save(candidate);
    }

    public Candidate createCandidate(Candidate candidate) {
        return candidateRepository.save(candidate);
    }

    public Candidate getCandidate(long id){
        return candidateRepository.getCandidateById(id);
    }

    public List<Candidate> getAll(){
        return candidateRepository.findAll();
    }

    public Candidate save(Candidate candidate){
        return candidateRepository.save(candidate);
    }

    public Optional<Candidate> findById(Long id) {
        return candidateRepository.findById(id);
    }

    public Optional<Candidate> findByUsername(String username) {
        return candidateRepository.findByUsername(username);
    }

    public Candidate update(Long id, Candidate updated) {
        Candidate existing = candidateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Candidate not fpund."));

        existing.setUsername(updated.getUsername());
        existing.setEmail(updated.getEmail());
        existing.setName(updated.getName());
        existing.setLastname(updated.getLastname());
        existing.setStartOfTraining(updated.getStartOfTraining());
        existing.setPreferredLocation(updated.getPreferredLocation());
        existing.setLocation(updated.getLocation());
        existing.setTheoryCompleted(updated.isTheoryCompleted());
        existing.setCategory(updated.getCategory());
        existing.setStatus(updated.getStatus());

        return candidateRepository.save(existing);
    }

    public void delete(Long id) {
        candidateRepository.deleteById(id);
    }

    public void enrollInClass(Long candidateId, Long classId) {
        candidateRepository.enrollCandidateInTheory(candidateId, classId);
    }

    public void completeAttendance(Long candidateId, Long classId) {
        candidateRepository.makeTheoryAttendanceCompleted(candidateId, classId);
    }

    public void unenrollFromClass(Long candidateId, Long classId) {
        candidateRepository.deleteAttendedRelationship(candidateId, classId);
    }
}
