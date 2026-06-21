package com.example.practical_class_es.controller;

import com.example.practical_class_es.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/report")
public class ReportController {



    @Autowired
    private ReportService reportService;

    @GetMapping
    public ResponseEntity<byte[]> generateReport() {
        try {
            byte[] pdf = reportService.generateReport();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=izvestaj-autoskola.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
