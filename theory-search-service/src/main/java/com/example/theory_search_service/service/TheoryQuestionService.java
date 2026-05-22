package com.example.theory_search_service.service;
import com.example.theory_search_service.model.TheoryQuestion;
import com.example.theory_search_service.repo.TheoryQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TheoryQuestionService {

    private final TheoryQuestionRepository theoryQuestionRepository;

    public TheoryQuestion save(TheoryQuestion question) {
        return theoryQuestionRepository.save(question);
    }

    public List<TheoryQuestion> saveAll(List<TheoryQuestion> questions) {
        List<TheoryQuestion> result = new ArrayList<>();
        theoryQuestionRepository.saveAll(questions).forEach(result::add);
        return result;
    }

    public Optional<TheoryQuestion> findById(String id) {
        return theoryQuestionRepository.findById(id);
    }

    public List<TheoryQuestion> findAll() {
        List<TheoryQuestion> result = new ArrayList<>();
        theoryQuestionRepository.findAll().forEach(result::add);
        return result;
    }

    public List<TheoryQuestion> findByLesson(int lessonOrderNumber) {
        return theoryQuestionRepository.findByLessonOrderNumber(lessonOrderNumber);
    }

    public List<TheoryQuestion> findByDifficulty(int difficultyLevel) {
        return theoryQuestionRepository.findByDifficultyLevel(difficultyLevel);
    }

    public List<TheoryQuestion> findByCategory(String category) {
        return theoryQuestionRepository.findByCategory(category);
    }

    public TheoryQuestion update(String id, TheoryQuestion updated) {
        theoryQuestionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + id));
        updated.setId(id);
        return theoryQuestionRepository.save(updated);
    }

    public void delete(String id) {
        theoryQuestionRepository.deleteById(id);
    }

    public void deleteAll() {
        theoryQuestionRepository.deleteAll();
    }
}
