package com.example.theory_organization.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@Getter
@Setter
@Node("Professor")
public class Professor extends User{
    String academicTitle;

    @Relationship(type = "LECTURES", direction = Relationship.Direction.OUTGOING)
    private List<TheoryClass> theoryClasses;
}
