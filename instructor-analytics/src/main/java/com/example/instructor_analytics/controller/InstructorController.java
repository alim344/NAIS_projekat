package com.example.instructor_analytics.controller;

import com.example.instructor_analytics.model.InstructorDocument;
import com.example.instructor_analytics.service.InstructorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/instructors")
public class InstructorController {

    @Autowired
    private InstructorService instructorService;

    @PostMapping
    public ResponseEntity<InstructorDocument> save(@RequestBody InstructorDocument instructor) {
        return new ResponseEntity<>(instructorService.saveInstructor(instructor), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstructorDocument> getById(@PathVariable String id) {
        Optional<InstructorDocument> instructor = instructorService.getInstructorById(id);
        return instructor.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Iterable<InstructorDocument>> getAll() {
        return ResponseEntity.ok(instructorService.getAllInstructors());
    }

    @PutMapping("/{id}")
    public ResponseEntity<InstructorDocument> update(@PathVariable String id,
                                                     @RequestBody InstructorDocument instructor) {
        return ResponseEntity.ok(instructorService.updateInstructor(id, instructor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        instructorService.deleteInstructor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/available")
    public ResponseEntity<Map<String, Object>> findAvailableInstructorsWithText(
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false) String category) {

        Map<String, Object> result = instructorService.findAvailableInstructorsWithText(searchText, category);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/assign-vehicle")
    public ResponseEntity<InstructorDocument> assignVehicleToInstructor(
            @PathVariable String id,
            @RequestBody Map<String, String> request) {
        String registrationNumber = request.get("registrationNumber");
        InstructorDocument updated = instructorService.assignVehicleToInstructor(id, registrationNumber);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/remove-vehicle")
    public ResponseEntity<InstructorDocument> removeVehicleFromInstructor(@PathVariable String id) {
        InstructorDocument updated = instructorService.removeVehicleFromInstructor(id);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @GetMapping("/by-email")
    public ResponseEntity<InstructorDocument> getInstructorByEmail(@RequestParam String email) {
        InstructorDocument instructor = instructorService.findByEmail(email);
        return instructor != null ? ResponseEntity.ok(instructor) : ResponseEntity.notFound().build();
    }


}
