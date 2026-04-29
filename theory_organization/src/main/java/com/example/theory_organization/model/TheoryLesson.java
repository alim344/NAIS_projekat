package com.example.theory_organization.model;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

@Getter
@Setter
@Node("TheoryLesson")
public class TheoryLesson {
    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private int orderNumber;
}
