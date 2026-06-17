package com.example.theory_saga.controller;

import com.example.theory_saga.dtos.CreateTheoryClassDTO;
import com.example.theory_saga.dtos.SagaResultDTO;
import com.example.theory_saga.service.TheoryClassSagaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/saga")
@RequiredArgsConstructor
public class TheoryClassSagaController {

    private final TheoryClassSagaService sagaService;

    @PostMapping("/schedule-class")
    public ResponseEntity<SagaResultDTO> scheduleClass(@RequestBody CreateTheoryClassDTO dto) {
        SagaResultDTO result = sagaService.scheduleTheoryClass(dto);
        return result.isSuccess()
                ? ResponseEntity.ok(result)
                : ResponseEntity.internalServerError().body(result);
    }

    @DeleteMapping("/cancel-class")
    public ResponseEntity<SagaResultDTO> cancelClass(
            @RequestParam Long neo4jClassId,
            @RequestParam String elasticsearchLogId) {
        SagaResultDTO result = sagaService.cancelTheoryClass(neo4jClassId, elasticsearchLogId);
        return result.isSuccess()
                ? ResponseEntity.ok(result)
                : ResponseEntity.internalServerError().body(result);
    }
}