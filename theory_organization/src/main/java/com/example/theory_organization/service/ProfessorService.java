package com.example.theory_organization.service;

import com.example.theory_organization.model.Professor;
import com.example.theory_organization.repo.ProfessorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfessorService {
    private final ProfessorRepository professorRepository;

    public Professor save(Professor professor) {
        return professorRepository.save(professor);
    }

    public List<Professor> findAll() {
        return professorRepository.findAll();
    }

    public Optional<Professor> findById(Long id) {
        return professorRepository.findById(id);
    }

    public Professor update(Long id, Professor updated) {
        Professor existing = professorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Professor not found."));

        existing.setUsername(updated.getUsername());
        existing.setEmail(updated.getEmail());
        existing.setName(updated.getName());
        existing.setLastname(updated.getLastname());
        existing.setAcademicTitle(updated.getAcademicTitle());

        return professorRepository.save(existing);
    }

    public void delete(Long id) {
        professorRepository.deleteById(id);
    }

    public void assignToClass(Long professorId, Long classId) {
        professorRepository.createLecturesRelationship(professorId, classId);
    }

    public void removeFromClass(Long professorId, Long classId) {
        professorRepository.deleteLecturesRelationship(professorId, classId);
    }

    public List<Map<String, Object>> getStats() {
        return professorRepository.getProfessorStats();
    }
}
