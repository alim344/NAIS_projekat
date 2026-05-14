package com.example.instructor_management.controller;


import com.example.instructor_management.DTO.InstructorDTO;
import com.example.instructor_management.model.Instructor;
import com.example.instructor_management.service.InstructorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/instructors")
@RequiredArgsConstructor
public class InstructorController {
    private final InstructorService instructorService;

    @GetMapping
    public List<Instructor> getAllInstructors() {
        return instructorService.getAllInstructors();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Instructor> getInstructorById(@PathVariable String id) {
        Instructor instructor = instructorService.getInstructorById(id);
        return instructor != null ? ResponseEntity.ok(instructor) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<String> createInstructor(@RequestBody InstructorDTO instructorDTO) {
        Instructor instructor = convertToEntity(instructorDTO);
        instructorService.saveInstructor(instructor);
        return ResponseEntity.ok("saved");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateInstructor(@PathVariable String id, @RequestBody InstructorDTO instructorDTO) {
        Instructor instructor = instructorService.getInstructorById(id);
        if (instructor == null) {
            return ResponseEntity.notFound().build();
        }

        instructor.setUsername(instructorDTO.getUsername());
        instructor.setEmail(instructorDTO.getEmail());
        instructor.setName(instructorDTO.getName());
        instructor.setLastname(instructorDTO.getLastname());
        instructor.setMaxCapacity(instructorDTO.getMaxCapacity());

        instructorService.saveInstructor(instructor);
        return ResponseEntity.ok("successful");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInstructor(@PathVariable String id) {
        instructorService.deleteInstructor(id);
        return ResponseEntity.noContent().build();
    }

    public Instructor convertToEntity(InstructorDTO dto) {
        Instructor instructor = new Instructor();
        instructor.setUsername(dto.getUsername());
        instructor.setPassword(dto.getPassword());
        instructor.setEmail(dto.getEmail());
        instructor.setName(dto.getName());
        instructor.setLastname(dto.getLastname());
        instructor.setMaxCapacity(dto.getMaxCapacity());
        return instructor;
    }


    @PostMapping("/{instructorId}/vehicles/{vehicleId}")
    public ResponseEntity<Void> assignVehicleToInstructor(
            @PathVariable String instructorId,
            @PathVariable String vehicleId,
            @RequestParam Integer mileageAtAssignment) {
        instructorService.assignVehicleToInstructor(instructorId, vehicleId, mileageAtAssignment);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/{instructorId}/candidates/{candidateId}")
    public ResponseEntity<Void> assignCandidateToInstructor(
            @PathVariable String instructorId,
            @PathVariable String candidateId,
            @RequestParam String status) {
        instructorService.assignCandidateToInstructor(instructorId, candidateId, status);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/with-candidate-count")
    public List<Map<String, Object>> getInstructorsWithCandidateCount() {
        return instructorService.getInstructorsWithCandidateCount();
    }

}
