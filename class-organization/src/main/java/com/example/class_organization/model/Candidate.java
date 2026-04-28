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

    public LocalDateTime getStartOfTraining() {
        return startOfTraining;
    }

    public void setStartOfTraining(LocalDateTime startOfTraining) {
        this.startOfTraining = startOfTraining;
    }

    public String getPreferredLocation() {
        return preferredLocation;
    }

    public void setPreferredLocation(String preferredLocation) {
        this.preferredLocation = preferredLocation;
    }

    public boolean isTheoryCompleted() {
        return theoryCompleted;
    }

    public void setTheoryCompleted(boolean theoryCompleted) {
        this.theoryCompleted = theoryCompleted;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public TrainingStatus getStatus() {
        return status;
    }

    public void setStatus(TrainingStatus status) {
        this.status = status;
    }

    public List<TimePreference> getTimePreference() {
        return timePreference;
    }

    public void setTimePreference(List<TimePreference> timePreference) {
        this.timePreference = timePreference;
    }

    public List<Attendance> getAttendanceList() {
        return attendanceList;
    }

    public void setAttendanceList(List<Attendance> attendanceList) {
        this.attendanceList = attendanceList;
    }
}
