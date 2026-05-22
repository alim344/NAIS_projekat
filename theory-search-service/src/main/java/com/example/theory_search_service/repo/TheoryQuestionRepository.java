package com.example.theory_search_service.repo;

import com.example.theory_search_service.model.TheoryQuestion;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TheoryQuestionRepository extends ElasticsearchRepository<TheoryQuestion, String> {
    List<TheoryQuestion> findByLessonOrderNumber(int lessonOrderNumber);
    List<TheoryQuestion> findByDifficultyLevel(int difficultyLevel);
    List<TheoryQuestion> findByCategory(String category);
}
