package com.example.theory_organization.controller;

import com.example.theory_organization.dto.ProfessorStatsDTO;
import com.example.theory_organization.model.Professor;
import com.example.theory_organization.service.ProfessorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/professors")
@RequiredArgsConstructor
public class ProfessorController {

    private final ProfessorService professorService;

    @PostMapping
    public ResponseEntity<Professor> create(@RequestBody Professor professor) {
        return ResponseEntity.ok(professorService.save(professor));
    }


    @PutMapping("/{id}")
    public ResponseEntity<Professor> update(@PathVariable Long id, @RequestBody Professor details) {
        return ResponseEntity.ok(professorService.update(id, details));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        professorService.delete(id);
        return ResponseEntity.ok("Profesor sa ID-em " + id + " je uspesno obrisan.");
    }

    @PostMapping("/{profId}/assign-to-class/{classId}")
    public ResponseEntity<String> assign(@PathVariable Long profId, @PathVariable Long classId) {
        professorService.assignToClass(profId, classId);
        return ResponseEntity.ok("Profesor je dodeljen casu (LECTURES).");
    }


    @GetMapping("/teaching-stats")
    public List<ProfessorStatsDTO> getStats() {
        return professorService.getStats();
    }
}
