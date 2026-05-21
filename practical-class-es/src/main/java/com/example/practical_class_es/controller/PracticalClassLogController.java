package com.example.practical_class_es.controller;

import com.example.practical_class_es.doc.PracticalClassLog;
import com.example.practical_class_es.service.PracticalClassLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/classlog")
public class PracticalClassLogController {

    @Autowired
    private PracticalClassLogService practicalClassLogService;

    @PostMapping("/save")
    public ResponseEntity<PracticalClassLog> create(@RequestBody PracticalClassLog log) {
        PracticalClassLog createdLog = practicalClassLogService.create(log);
        return new ResponseEntity<>(createdLog, HttpStatus.CREATED);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<PracticalClassLog> findById(@PathVariable String id) {
        return practicalClassLogService.findById(id)
                .map(log -> new ResponseEntity<>(log, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/getALl")
    public ResponseEntity<List<PracticalClassLog>> findAll() {
        List<PracticalClassLog> logs = practicalClassLogService.findAll();
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<PracticalClassLog> update(@PathVariable String id, @RequestBody PracticalClassLog updatedLog) {
        try {
            PracticalClassLog result = practicalClassLogService.update(id, updatedLog);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        practicalClassLogService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
