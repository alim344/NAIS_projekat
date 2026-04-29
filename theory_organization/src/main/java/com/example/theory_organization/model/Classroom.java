package com.example.theory_organization.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Getter
@Setter
@Node("Classroom")
public class Classroom {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private int capacity;
}
