package com.example.class_organization.model;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDateTime;
import java.util.List;

@Node("Candidate")
public class Candidate extends  User{


    private LocalDateTime startOfTraining;
    private String preferredLocation;
    private boolean theoryCompleted;
    private Category category;
    private TrainingStatus status;

    @Relationship(type = "HAS_PREFERENCE", direction = Relationship.Direction.OUTGOING)
    private List<TimePreference> timePreference;

    @Relationship(type = "ATTENDS", direction = Relationship.Direction.OUTGOING)
    private List<Attendance> attendanceList;

}
