package com.example.instructor_management.service;

import com.example.instructor_management.model.InstructorDocuments;
import com.example.instructor_management.repository.InstructorDocumentsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class InstructorDocumentsService {

    private final InstructorDocumentsRepository documentsRepository;

    public InstructorDocuments createDocument(InstructorDocuments document) {
        return documentsRepository.save(document);
    }

    public List<InstructorDocuments> getAllDocuments() {
        return documentsRepository.findAll();
    }

    public InstructorDocuments getDocumentById(String id) {
        return documentsRepository.findById(id).orElse(null);
    }

    public InstructorDocuments updateDocument(String id, InstructorDocuments updatedDocument) {
        InstructorDocuments existing = documentsRepository.findById(id).orElse(null);
        if (existing == null) return null;

        existing.setDocumentType(updatedDocument.getDocumentType());
        existing.setExpiryDate(updatedDocument.getExpiryDate());

        return documentsRepository.save(existing);
    }

    public void deleteDocument(String id) {
        documentsRepository.deleteById(id);
    }


}
