package com.example.theory_organization.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.LocalDateTime;

@Getter
@Setter
@RelationshipProperties
public class AttendanceTheory {
    @RelationshipId
    private Long id;

    @TargetNode
    private TheoryClass theoryClass;

    private boolean completed = false;
    private String note;
    private LocalDateTime arrivalTime;
}
