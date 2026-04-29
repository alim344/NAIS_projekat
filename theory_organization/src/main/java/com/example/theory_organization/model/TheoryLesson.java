package com.example.theory_organization.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

@Getter
@Setter
@AllArgsConstructor
@Node("TheoryLesson")
public class TheoryLesson {
    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private int orderNumber;
}
