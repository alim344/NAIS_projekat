package com.example.theory_organization.controller;

import com.example.theory_organization.dto.RegistrationDTO;
import com.example.theory_organization.model.Candidate;
import com.example.theory_organization.model.TheoryLesson;
import com.example.theory_organization.service.CandidateService;
import com.example.theory_organization.service.TheoryLessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidates")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;
    private final TheoryLessonService theoryLessonService;

    @PostMapping("/register")
    public ResponseEntity<Candidate> register(@RequestBody RegistrationDTO dto) {
        return ResponseEntity.ok(candidateService.registerCandidate(dto));
    }

    @GetMapping
    public List<Candidate> getAll() {
        return candidateService.getAll();
    }


    @PostMapping("/{candId}/enroll/{classId}")
    public ResponseEntity<String> enroll(@PathVariable Long candId, @PathVariable Long classId) {
        candidateService.enrollInClass(candId, classId);
        return ResponseEntity.ok("Kandidat je uspesno upisan na cas (grana kreirana).");
    }

    @DeleteMapping("/{candId}/unenroll/{classId}")
    public ResponseEntity<String> unenroll(@PathVariable Long candId, @PathVariable Long classId) {
        candidateService.unenrollFromClass(candId, classId);
        return ResponseEntity.ok("Veza ATTENDED_THEORY je obrisana.");
    }


    @PatchMapping("/{candId}/complete-attendance/{classId}")
    public ResponseEntity<String> completeAttendance(@PathVariable Long candId, @PathVariable Long classId) {
        candidateService.completeAttendance(candId, classId);
        return ResponseEntity.ok("Prisustvo potvrdjeno: completed=true, arrivalTime postavljen.");
    }


    @GetMapping("/{username}/next-missing-lesson")
    public ResponseEntity<TheoryLesson> getNextMissing(@PathVariable String username) {
        TheoryLesson lesson = theoryLessonService.getNextMissing(username);
        return lesson != null ? ResponseEntity.ok(lesson) : ResponseEntity.noContent().build();
    }
}
