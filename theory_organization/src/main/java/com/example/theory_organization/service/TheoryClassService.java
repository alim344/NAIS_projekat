package com.example.theory_organization.service;


import com.example.theory_organization.model.Classroom;
import com.example.theory_organization.model.TheoryClass;
import com.example.theory_organization.repo.ProfessorRepository;
import com.example.theory_organization.repo.TheoryClassRepository;
import com.example.theory_organization.repo.TheoryLessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TheoryClassService {

    @Autowired
    private TheoryClassRepository theoryClassRepository;

    @Autowired
    private TheoryLessonRepository theoryLessonRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    public List<TheoryClass> getAllClasses() {
        return theoryClassRepository.findAll();
    }

    public TheoryClass saveClass(TheoryClass theoryClass) {
        return theoryClassRepository.save(theoryClass);
    }

    public Optional<TheoryClass> findById(Long id) {
        return theoryClassRepository.findById(id);
    }

    public void deleteClass(TheoryClass theoryClass) {
        theoryClassRepository.delete(theoryClass);
    }

    public TheoryClass update(Long id, TheoryClass updated) {
        TheoryClass existing = theoryClassRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("TheoryClass not found."));

        existing.setStartTime(updated.getStartTime());
        existing.setEndTime(updated.getEndTime());
        existing.setTheoryLesson(updated.getTheoryLesson());
        existing.setHall(updated.getHall());

        return theoryClassRepository.save(existing);
    }

    public void setClassHall(Long classId, Long hallId) {
        theoryClassRepository.createHeldInRelationship(classId, hallId);
    }

    public void setClassLesson(Long classId, Long lessonId) {
        theoryClassRepository.createInstanceOfRelationship(classId, lessonId);
    }

    public void removeHall(Long classId) {
        theoryClassRepository.deleteHeldInRelationship(classId);
    }

    public List<TheoryClass> findOverbooked() {
        return theoryClassRepository.findOverbookedClasses();
    }

    public List<Classroom> findAvailableHalls(LocalDateTime time) {
        return theoryClassRepository.findAvailableClassrooms(time);
    }

}
