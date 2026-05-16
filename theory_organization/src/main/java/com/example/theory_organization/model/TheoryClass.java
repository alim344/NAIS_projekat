package com.example.theory_organization.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Node("TheoryClass")
public class TheoryClass {

    @Id
    @GeneratedValue
    private Long id;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Relationship(type = "INSTANCE_OF", direction = Relationship.Direction.OUTGOING)
    private TheoryLesson theoryLesson;

    @Relationship(type = "HELD_IN", direction = Relationship.Direction.OUTGOING)
    private Classroom hall;


}
