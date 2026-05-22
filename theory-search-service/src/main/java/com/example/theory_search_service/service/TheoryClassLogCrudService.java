package com.example.theory_search_service.service;

import com.example.theory_search_service.model.TheoryClassLog;
import com.example.theory_search_service.repo.TheoryClassLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TheoryClassLogCrudService {

    private final TheoryClassLogRepository theoryClassLogRepository;

    public TheoryClassLog save(TheoryClassLog log) {
        return theoryClassLogRepository.save(log);
    }

    public List<TheoryClassLog> saveAll(List<TheoryClassLog> logs) {
        List<TheoryClassLog> result = new ArrayList<>();
        theoryClassLogRepository.saveAll(logs).forEach(result::add);
        return result;
    }

    public Optional<TheoryClassLog> findById(String id) {
        return theoryClassLogRepository.findById(id);
    }

    public List<TheoryClassLog> findAll() {
        List<TheoryClassLog> result = new ArrayList<>();
        theoryClassLogRepository.findAll().forEach(result::add);
        return result;
    }

    public List<TheoryClassLog> findByProfessor(String professorUsername) {
        return theoryClassLogRepository.findByProfessorUsername(professorUsername);
    }

    public List<TheoryClassLog> findByClassroom(String classroomName) {
        return theoryClassLogRepository.findByClassroomName(classroomName);
    }

    public List<TheoryClassLog> findByLesson(int lessonOrderNumber) {
        return theoryClassLogRepository.findByLessonOrderNumber(lessonOrderNumber);
    }

    public List<TheoryClassLog> findByCategory(String category) {
        return theoryClassLogRepository.findByCategory(category);
    }

    public TheoryClassLog update(String id, TheoryClassLog updated) {
        theoryClassLogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Log not found: " + id));
        updated.setId(id);
        return theoryClassLogRepository.save(updated);
    }

    public void delete(String id) {
        theoryClassLogRepository.deleteById(id);
    }

    public void deleteAll() {
        theoryClassLogRepository.deleteAll();
    }
}
