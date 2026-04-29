package com.example.theory_organization.controller;

import com.example.theory_organization.model.Classroom;
import com.example.theory_organization.model.TheoryClass;
import com.example.theory_organization.service.TheoryClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/theory-classes")
@RequiredArgsConstructor
public class TheoryClassController {
    private final TheoryClassService theoryClassService;

    @PostMapping
    public ResponseEntity<TheoryClass> create(@RequestBody TheoryClass tc) {
        return ResponseEntity.ok(theoryClassService.saveClass(tc));
    }

    @PostMapping("/{classId}/setup")
    public ResponseEntity<String> setupClass(@PathVariable Long classId,
                                             @RequestParam Long hallId,
                                             @RequestParam Long lessonId) {
        theoryClassService.setClassHall(classId, hallId);
        theoryClassService.setClassLesson(classId, lessonId);
        return ResponseEntity.ok("Sala i lekcija povezane).");
    }


    @PutMapping("/{id}")
    public ResponseEntity<TheoryClass> update(@PathVariable Long id, @RequestBody TheoryClass details) {
        return ResponseEntity.ok(theoryClassService.update(id, details));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        theoryClassService.delete(id);
        return ResponseEntity.ok("Teorijski cas je otkazan/obrisan.");
    }
    
    @GetMapping("/overbooked")
    public List<TheoryClass> getOverbooked() {
        return theoryClassService.findOverbooked();
    }

    @GetMapping("/available-halls")
    public List<Classroom> getAvailableHalls(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time) {
        return theoryClassService.findAvailableHalls(time);
    }
}
