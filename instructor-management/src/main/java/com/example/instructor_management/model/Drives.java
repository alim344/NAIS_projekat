package com.example.instructor_management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RelationshipProperties
public class Drives {

    @RelationshipId
    private String id;

    private LocalDate assignedDate;

    private Integer mileageAtAssignment;

    @TargetNode
    private Vehicle vehicle;
}
