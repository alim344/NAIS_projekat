package com.example.theory_organization.service;

import com.example.theory_organization.model.Classroom;
import com.example.theory_organization.repo.ClassroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClassroomService {

    private final ClassroomRepository classroomRepository;

    public List<Classroom> getAllClassrooms() {
        return classroomRepository.findAll();
    }

    public Optional<Classroom> getClassroomById(Long id) {
        return classroomRepository.findById(id);
    }

    public Classroom update(Long id, Classroom updated) {
        Classroom existing = classroomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Classroom not found."));

        existing.setName(updated.getName());
        existing.setCapacity(updated.getCapacity());

        return classroomRepository.save(existing);
    }

    public void delete(Long id) {
        classroomRepository.deleteById(id);
    }

    public void save(Classroom sala) {
        classroomRepository.save(sala);
    }
}
