package com.example.class_organization.dto;

import java.time.LocalDateTime;

public class PracticalClassDTO {

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private boolean completed;

    private String candidate_username;


    private String instructor_username;


    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getCandidate_username() {
        return candidate_username;
    }

    public void setCandidate_username(String candidate_username) {
        this.candidate_username = candidate_username;
    }

    public String getInstructor_username() {
        return instructor_username;
    }

    public void setInstructor_username(String instructor_username) {
        this.instructor_username = instructor_username;
    }
}
