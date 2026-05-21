package com.example.practical_class_es.controller;

import com.example.practical_class_es.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/complex")
public class ComplexQController {

    @Autowired
    private QueryService queryService;

    @GetMapping("/classes")
    public ResponseEntity<Map<String, Object>> searchClasses(
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false) Integer minScore,
            @RequestParam(required = false) Integer minKm,
            @RequestParam(required = false) Integer maxKm,
            @RequestParam(defaultValue = "true") Boolean onlyCompleted) {

        Map<String, Object> responseData = queryService.searchClassesByTextAndPerformance(
                searchText,
                minScore,
                minKm,
                maxKm,
                onlyCompleted
        );

        return ResponseEntity.ok(responseData);
    }




    @GetMapping("/scheduling")
    public ResponseEntity<Map<String, Object>> findCandidatesForScheduling(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minAvgGrade,
            @RequestParam(required = false) Integer minClasses,
            @RequestParam(required = false) String prefDate) {

        Map<String, Object> responseData = queryService.findCandidatesForScheduling(
                category,
                minAvgGrade,
                minClasses,
                prefDate
        );

        return ResponseEntity.ok(responseData);
    }


    @GetMapping("/problematic")
    public ResponseEntity<Map<String, Object>> findProblematicClasses(
            @RequestParam(required = false) Integer maxScore,
            @RequestParam(required = false) Integer minKm,
            @RequestParam(required = false) Long instructorId) {

        Map<String, Object> responseData = queryService.findProblematicClasses(
                maxScore,
                minKm,
                instructorId
        );

        return ResponseEntity.ok(responseData);
    }





}
