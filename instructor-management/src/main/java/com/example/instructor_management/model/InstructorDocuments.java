package com.example.instructor_management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Node("Document")
public class InstructorDocuments {

    @Id
    @GeneratedValue
    private String id;

    private String documentType;

    private LocalDate expiryDate;

}
