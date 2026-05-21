package com.example.practical_class_es.controller;

import com.example.practical_class_es.doc.CandidateAnalytics;
import com.example.practical_class_es.service.CandidateAnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/candidate")
public class CandidateAnalyticsController {

    @Autowired
    private CandidateAnalyticsService candidateAnalyticsService;

    @PostMapping("/save")
    public ResponseEntity<CandidateAnalytics> create(@RequestBody CandidateAnalytics candidate) {
        CandidateAnalytics createdCandidate = candidateAnalyticsService.create(candidate);
        return new ResponseEntity<>(createdCandidate, HttpStatus.CREATED);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<CandidateAnalytics> findById(@PathVariable String id) {
        return candidateAnalyticsService.findById(id)
                .map(candidate -> new ResponseEntity<>(candidate, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<CandidateAnalytics>> findAll() {
        List<CandidateAnalytics> analyticsList = candidateAnalyticsService.findAll();
        return new ResponseEntity<>(analyticsList, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CandidateAnalytics> update(@PathVariable String id, @RequestBody CandidateAnalytics updatedCandidate) {
        try {
            CandidateAnalytics result = candidateAnalyticsService.update(id, updatedCandidate);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        candidateAnalyticsService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
