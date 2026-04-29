package com.example.theory_organization.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Node("Candidate")
public class Candidate extends User{
    private LocalDateTime startOfTraining;
    private String preferredLocation;
    private String location;
    private boolean theoryCompleted;
    private Category category;
    private TrainingStatus status;

    @Relationship(type = "HAS_PREFERENCE", direction = Relationship.Direction.OUTGOING)
    private List<TimePreference> timePreferences;

    @Relationship(type = "ATTENDED_THEORY", direction = Relationship.Direction.OUTGOING)
    private List<AttendanceTheory> attendanceTheoryList;
}
