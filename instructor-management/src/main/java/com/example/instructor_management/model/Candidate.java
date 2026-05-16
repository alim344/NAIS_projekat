package com.example.instructor_management.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Node;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Node("Candidate")
public class Candidate extends User {

    private boolean theoryCompleted;

    private Category category;

    private TrainingStatus trainingStatus;
}
