package com.example.instructor_analytics.service;

import com.example.instructor_analytics.model.InstructorDocument;
import com.example.instructor_analytics.repository.InstructorRepository;
import com.example.instructor_analytics.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InstructorService {

    private final InstructorRepository instructorRepository;

    public InstructorDocument saveInstructor(InstructorDocument instructor) {
        return instructorRepository.save(instructor);
    }

    public Optional<InstructorDocument> getInstructorById(String id) {
        return instructorRepository.findById(id);
    }

    public Iterable<InstructorDocument> getAllInstructors() {
        return instructorRepository.findAll();
    }

    public InstructorDocument updateInstructor(String id, InstructorDocument updated) {
        updated.setId(id);
        return instructorRepository.save(updated);
    }

    public void deleteInstructor(String id) {
        instructorRepository.deleteById(id);
    }

    public long countInstructors() {
        return instructorRepository.count();
    }
}
