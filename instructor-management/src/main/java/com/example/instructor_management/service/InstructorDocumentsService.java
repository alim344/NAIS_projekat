package com.example.instructor_management.service;

import com.example.instructor_management.DTO.InstructorDocumentsDTO;
import com.example.instructor_management.model.InstructorDocuments;
import com.example.instructor_management.repository.InstructorDocumentsRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class InstructorDocumentsService {

    private final InstructorDocumentsRepository documentsRepository;

    public InstructorDocumentsDTO toDTO(InstructorDocuments document) {
        return new InstructorDocumentsDTO(
                document.getId(),
                document.getDocumentType(),
                document.getExpiryDate()
        );
    }

    public List<InstructorDocumentsDTO> toDTOList(List<InstructorDocuments> documents) {
        return documents.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public InstructorDocuments toEntity(InstructorDocumentsDTO dto) {
        InstructorDocuments document = new InstructorDocuments();
        document.setDocumentType(dto.getDocumentType());
        document.setExpiryDate(dto.getExpiryDate());
        return document;
    }

    public InstructorDocumentsDTO createDocument(InstructorDocumentsDTO createDTO) {
        InstructorDocuments document = toEntity(createDTO);
        return toDTO(documentsRepository.save(document));
    }

    public List<InstructorDocumentsDTO> getAllDocuments() {
        return toDTOList(documentsRepository.findAll());
    }

    public InstructorDocumentsDTO getDocumentById(String id) {
        return documentsRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    public InstructorDocumentsDTO updateDocument(String id, InstructorDocumentsDTO updateDTO) {
        InstructorDocuments existing = documentsRepository.findById(id).orElse(null);
        if (existing == null) return null;

        existing.setDocumentType(updateDTO.getDocumentType());
        existing.setExpiryDate(updateDTO.getExpiryDate());

        return toDTO(documentsRepository.save(existing));
    }

    public void deleteDocument(String id) {
        documentsRepository.deleteById(id);
    }


    private final Neo4jClient neo4jClient;

    public List<Map<String, Object>> countExpiringDocumentsByType() {
        String query = "MATCH (d:Document) " +
                "WHERE d.expiryDate <= date() + duration({days: 60}) " +
                "WITH d.documentType AS type, COUNT(d) AS documentCount " +
                "RETURN type, documentCount " +
                "ORDER BY documentCount DESC";

        return new ArrayList<>(neo4jClient.query(query).fetch().all());
    }

    @Transactional
    public void assignDocumentToInstructor(String instructorId, String documentId) {
        String query = "MATCH (i:Instructor), (d:Document) " +
                "WHERE elementId(i) = $instructorId AND elementId(d) = $documentId " +
                "CREATE (i)-[:HAS_DOCUMENT]->(d)";

        neo4jClient.query(query)
                .bind(instructorId).to("instructorId")
                .bind(documentId).to("documentId")
                .run();
    }


}
