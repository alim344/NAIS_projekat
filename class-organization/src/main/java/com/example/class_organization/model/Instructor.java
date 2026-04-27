package com.example.class_organization.model;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@Node("Instructor")
public class Instructor extends User{

    @Relationship(type = "DRIVES", direction = Relationship.Direction.OUTGOING)
    private VehicleAssignment vehicleAssignment;

    @Relationship(type = "TRAINS", direction = Relationship.Direction.OUTGOING)
    private List<Candidate> candidates;

    private Integer maxCapacity;

    @Relationship(type = "TEACHES", direction = Relationship.Direction.OUTGOING)
    private List<Teaching> teachingList;

}
