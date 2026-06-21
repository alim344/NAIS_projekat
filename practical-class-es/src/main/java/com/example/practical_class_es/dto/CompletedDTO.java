package com.example.practical_class_es.dto;

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

    private Double consumedFuelLiters;
    private String inst_name;
    private String inst_lastName;

    private String cand_name;
    private String cand_lastName;
    private String category;

    private String registration;
    private boolean malfunction;

    public CompletedDTO() {
    }

    public CompletedDTO(Long practicalClassId, Long candidateId, Long instructorId, Long vehicleId, Integer kmDriven, int score, String startTime, String endTime, String instructorNote, Double consumedFuelLiters, String inst_name, String inst_lastName, String cand_name, String cand_lastName, String category) {
        this.practicalClassId = practicalClassId;
        this.candidateId = candidateId;
        this.instructorId = instructorId;
        this.vehicleId = vehicleId;
        this.kmDriven = kmDriven;
        this.score = score;
        this.startTime = startTime;
        this.endTime = endTime;
        this.instructorNote = instructorNote;
        this.consumedFuelLiters = consumedFuelLiters;
        this.inst_name = inst_name;
        this.inst_lastName = inst_lastName;
        this.cand_name = cand_name;
        this.cand_lastName = cand_lastName;
        this.category = category;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public boolean isMalfunction() {
        return malfunction;
    }

    public void setMalfunction(boolean malfunction) {
        this.malfunction = malfunction;
    }

    public Double getConsumedFuelLiters() {
        return consumedFuelLiters;
    }

    public void setConsumedFuelLiters(Double consumedFuelLiters) {
        this.consumedFuelLiters = consumedFuelLiters;
    }

    public String getInst_name() {
        return inst_name;
    }

    public void setInst_name(String inst_name) {
        this.inst_name = inst_name;
    }

    public String getInst_lastName() {
        return inst_lastName;
    }

    public void setInst_lastName(String inst_lastName) {
        this.inst_lastName = inst_lastName;
    }

    public String getCand_name() {
        return cand_name;
    }

    public void setCand_name(String cand_name) {
        this.cand_name = cand_name;
    }

    public String getCand_lastName() {
        return cand_lastName;
    }

    public void setCand_lastName(String cand_lastName) {
        this.cand_lastName = cand_lastName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
