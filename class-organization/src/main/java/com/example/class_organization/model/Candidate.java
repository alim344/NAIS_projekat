package com.example.class_organization.model;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Node("Candidate")
public class Candidate extends  User{


    private String startOfTraining;
    private String preferredLocation;
    private boolean theoryCompleted;
    private Category category;
    private TrainingStatus status;

    @Relationship(type = "HAS_PREFERENCE", direction = Relationship.Direction.OUTGOING)
    private List<TimePreference> timePreferences;

    @Relationship(type = "ATTENDS", direction = Relationship.Direction.OUTGOING)
    private List<Attendance> attendanceList;


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

    public List<TimePreference> getTimePreferences() {
        return timePreferences;
    }

    public void setTimePreferences(List<TimePreference> timePreferences) {
        this.timePreferences = timePreferences;
    }

    public List<Attendance> getAttendanceList() {
        return attendanceList;
    }

    public void setAttendanceList(List<Attendance> attendanceList) {
        this.attendanceList = attendanceList;
    }


    public LocalDateTime getStartOfTrainingAsDateTime() {
        if (this.startOfTraining == null) return null;

        return LocalDateTime.parse(this.startOfTraining.replace("Z", ""));
    }

    public void setStartOfTrainingFromDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            this.startOfTraining = null;
        } else {
            this.startOfTraining = dateTime.toString();
        }
    }

    public String getStartOfTraining() {
        return startOfTraining;
    }

    public void setStartOfTraining(String startOfTraining) {
        this.startOfTraining = startOfTraining;
    }
}
