package com.example.theory_organization.service;

import com.example.theory_organization.model.TheoryLesson;
import com.example.theory_organization.repo.TheoryLessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TheoryLessonService {
    private final TheoryLessonRepository theoryLessonRepository;

    public List<TheoryLesson> getAllSorted() {
        return theoryLessonRepository.findAllByOrderByOrderNumberAsc();
    }

    public TheoryLesson getNextMissing(String username) {
        return theoryLessonRepository.findFirstMissingLesson(username)
                .orElse(null);
    }

    public void save(TheoryLesson l1) {
        theoryLessonRepository.save(l1);
    }
}
