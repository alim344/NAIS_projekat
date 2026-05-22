package com.example.theory_search_service.repo;

import com.example.theory_search_service.model.TheoryClassLog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TheoryClassLogRepository extends ElasticsearchRepository<TheoryClassLog, String> {
    List<TheoryClassLog> findByProfessorUsername(String professorUsername);
    List<TheoryClassLog> findByClassroomName(String classroomName);
    List<TheoryClassLog> findByLessonOrderNumber(int lessonOrderNumber);
    List<TheoryClassLog> findByCategory(String category);
}
