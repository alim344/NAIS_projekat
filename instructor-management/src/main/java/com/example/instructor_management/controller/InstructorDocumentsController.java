package com.example.instructor_management.controller;

import com.example.instructor_management.DTO.InstructorDocumentsDTO;
import com.example.instructor_management.service.InstructorDocumentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class InstructorDocumentsController {
    private final InstructorDocumentsService documentsService;

    @GetMapping
    public List<InstructorDocumentsDTO> getAllDocuments() {
        return documentsService.getAllDocuments();
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstructorDocumentsDTO> getDocumentById(@PathVariable String id) {
        InstructorDocumentsDTO document = documentsService.getDocumentById(id);
        return document != null ? ResponseEntity.ok(document) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public InstructorDocumentsDTO createDocument(@RequestBody InstructorDocumentsDTO createDTO) {
        return documentsService.createDocument(createDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InstructorDocumentsDTO> updateDocument(
            @PathVariable String id,
            @RequestBody InstructorDocumentsDTO updateDTO) {
        InstructorDocumentsDTO updated = documentsService.updateDocument(id, updateDTO);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable String id) {
        documentsService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/expiring-count-by-type")
    public List<Map<String, Object>> getExpiringDocumentsCountByType() {
        return documentsService.countExpiringDocumentsByType();
    }

    @PostMapping("/{instructorId}/assign/{documentId}")
    public ResponseEntity<Void> assignDocumentToInstructor(
            @PathVariable String instructorId,
            @PathVariable String documentId) {
        documentsService.assignDocumentToInstructor(instructorId, documentId);
        return ResponseEntity.ok().build();
    }
}
