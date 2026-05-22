package com.example.theory_search_service.controller;

import com.example.theory_search_service.model.TheoryClassLog;
import com.example.theory_search_service.service.TheoryClassLogCrudService;
import com.example.theory_search_service.service.TheorySearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/class-logs")
@RequiredArgsConstructor
public class TheoryClassLogController {

    private final TheoryClassLogCrudService crudService;
    private final TheorySearchService theorySearchService;

    @PostMapping
    public ResponseEntity<TheoryClassLog> create(@RequestBody TheoryClassLog log) {
        return ResponseEntity.ok(crudService.save(log));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<TheoryClassLog>> createBulk(@RequestBody List<TheoryClassLog> logs) {
        return ResponseEntity.ok(crudService.saveAll(logs));
    }

    @GetMapping
    public ResponseEntity<List<TheoryClassLog>> getAll() {
        return ResponseEntity.ok(crudService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TheoryClassLog> getById(@PathVariable String id) {
        return crudService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-professor/{username}")
    public ResponseEntity<List<TheoryClassLog>> getByProfessor(@PathVariable String username) {
        return ResponseEntity.ok(crudService.findByProfessor(username));
    }

    @GetMapping("/by-classroom/{name}")
    public ResponseEntity<List<TheoryClassLog>> getByClassroom(@PathVariable String name) {
        return ResponseEntity.ok(crudService.findByClassroom(name));
    }

    @GetMapping("/by-lesson/{orderNumber}")
    public ResponseEntity<List<TheoryClassLog>> getByLesson(@PathVariable int orderNumber) {
        return ResponseEntity.ok(crudService.findByLesson(orderNumber));
    }

    @GetMapping("/by-category/{category}")
    public ResponseEntity<List<TheoryClassLog>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(crudService.findByCategory(category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TheoryClassLog> update(@PathVariable String id,
                                                 @RequestBody TheoryClassLog log) {
        return ResponseEntity.ok(crudService.update(id, log));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        crudService.delete(id);
        return ResponseEntity.ok("Deleted: " + id);
    }

    @GetMapping("/analyze-by-professor")
    public ResponseEntity<Map<String, Object>> analyzeByProfessor(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @RequestParam(required = false) String professorUsername,
            @RequestParam(required = false) Integer minCandidates,
            @RequestParam(required = false) Boolean onlyFullyAttended) {
        return ResponseEntity.ok(theorySearchService
                .analyzeClassesByDateAndProfessor(fromDate, toDate, professorUsername, minCandidates, onlyFullyAttended));
    }

    @GetMapping("/analyze-classroom-usage")
    public ResponseEntity<Map<String, Object>> analyzeClassroomUsage(
            @RequestParam(required = false) String classroomName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @RequestParam(required = false) Integer minDurationMinutes) {
        return ResponseEntity.ok(theorySearchService
                .analyzeClassroomUsage(classroomName, fromDate, toDate, minDurationMinutes));
    }
}
