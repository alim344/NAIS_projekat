package com.example.class_organization.model;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.time.LocalDateTime;

@Node("PracticalClass")
public class PracticalClass {


    @Id
    @GeneratedValue
    private Long id;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private boolean completed;

}
