package com.example.class_organization.dto;

import java.io.Serializable;


public class CompletedDTO implements Serializable {

    private Long practicalClassId;
    private Long candidateId;
    private Long instructorId;
    private Long vehicleId;
    private Integer kmDriven;
    private int score;
    private String startTime;
    private String endTime;
    private String instructorNote;

    public CompletedDTO() {
    }

    public CompletedDTO(Long practicalClassId, Long candidateId, String instructorNote, Integer kmDriven, Long vehicleId, Long instructorId, int score, String startTime, String endTime) {
        this.practicalClassId = practicalClassId;
        this.candidateId = candidateId;
        this.instructorNote = instructorNote;
        this.kmDriven = kmDriven;
        this.vehicleId = vehicleId;
        this.instructorId = instructorId;
        this.score = score;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getPracticalClassId() {
        return practicalClassId;
    }

    public void setPracticalClassId(Long practicalClassId) {
        this.practicalClassId = practicalClassId;
    }

    public String getInstructorNote() {
        return instructorNote;
    }

    public void setInstructorNote(String instructorNote) {
        this.instructorNote = instructorNote;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Integer getKmDriven() {
        return kmDriven;
    }

    public void setKmDriven(Integer kmDriven) {
        this.kmDriven = kmDriven;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Long getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(Long instructorId) {
        this.instructorId = instructorId;
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }
}
