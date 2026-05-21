package com.example.theory_search_service.controller;

import com.example.theory_search_service.model.TheoryQuestion;
import com.example.theory_search_service.service.TheoryQuestionService;
import com.example.theory_search_service.service.TheorySearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class TheoryQuestionController {

    private final TheoryQuestionService theoryQuestionService;
    private final TheorySearchService theorySearchService;

    @PostMapping
    public ResponseEntity<TheoryQuestion> create(@RequestBody TheoryQuestion question) {
        return ResponseEntity.ok(theoryQuestionService.save(question));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<TheoryQuestion>> createBulk(@RequestBody List<TheoryQuestion> questions) {
        return ResponseEntity.ok(theoryQuestionService.saveAll(questions));
    }

    @GetMapping
    public ResponseEntity<List<TheoryQuestion>> getAll() {
        return ResponseEntity.ok(theoryQuestionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TheoryQuestion> getById(@PathVariable Long id) {
        return theoryQuestionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-lesson/{orderNumber}")
    public ResponseEntity<List<TheoryQuestion>> getByLesson(@PathVariable int orderNumber) {
        return ResponseEntity.ok(theoryQuestionService.findByLesson(orderNumber));
    }

    @GetMapping("/by-difficulty/{level}")
    public ResponseEntity<List<TheoryQuestion>> getByDifficulty(@PathVariable int level) {
        return ResponseEntity.ok(theoryQuestionService.findByDifficulty(level));
    }

    @GetMapping("/by-category/{category}")
    public ResponseEntity<List<TheoryQuestion>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(theoryQuestionService.findByCategory(category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TheoryQuestion> update(@PathVariable Long id,
                                                 @RequestBody TheoryQuestion question) {
        return ResponseEntity.ok(theoryQuestionService.update(id, question));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        theoryQuestionService.delete(id);
        return ResponseEntity.ok("Deleted: " + id);
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) Integer lessonOrderNumber,
            @RequestParam(required = false) Integer minDifficulty,
            @RequestParam(required = false) Integer maxDifficulty,
            @RequestParam(required = false) String category) {
        return ResponseEntity.ok(theorySearchService
                .searchQuestionsWithStats(text, lessonOrderNumber, minDifficulty, maxDifficulty, category));
    }
}
