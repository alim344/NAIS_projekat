package com.example.practical_class_es.doc;


import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@Document(indexName = "practical-class-log")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PracticalClassLog implements Serializable {

    @Id
    private String id;
    @Field(type = FieldType.Long)
    private Long practicalClassId; // ID iz tvoje Neo4j baze radi sinhronizacije

    @Field(type = FieldType.Keyword)
    private String startTime;

    @Field(type = FieldType.Keyword)
    private String endTime;

    @Field(type = FieldType.Boolean)
    private boolean completed;

    @Field(type = FieldType.Integer)
    private Integer kmDriven;

    @Field(type = FieldType.Integer)
    private int score;

    @Field(type = FieldType.Double)
    private Double consumedFuelLiters;


    @Field(type = FieldType.Object)
    private InstructorInfo instructorInfo;

    @Field(type = FieldType.Object)
    private CandidateInfo candidateInfo;

    @Field(type = FieldType.Object)
    private VehicleInfo vehicleInfo;


    @Field(type = FieldType.Text, analyzer = "standard")
    private String instructorNote;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String route;

    public PracticalClassLog() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getPracticalClassId() {
        return practicalClassId;
    }

    public void setPracticalClassId(Long practicalClassId) {
        this.practicalClassId = practicalClassId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Integer getKmDriven() {
        return kmDriven;
    }

    public void setKmDriven(Integer kmDriven) {
        this.kmDriven = kmDriven;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Double getConsumedFuelLiters() {
        return consumedFuelLiters;
    }

    public void setConsumedFuelLiters(Double consumedFuelLiters) {
        this.consumedFuelLiters = consumedFuelLiters;
    }

    public InstructorInfo getInstructorInfo() {
        return instructorInfo;
    }

    public void setInstructorInfo(InstructorInfo instructorInfo) {
        this.instructorInfo = instructorInfo;
    }

    public CandidateInfo getCandidateInfo() {
        return candidateInfo;
    }

    public void setCandidateInfo(CandidateInfo candidateInfo) {
        this.candidateInfo = candidateInfo;
    }

    public VehicleInfo getVehicleInfo() {
        return vehicleInfo;
    }

    public void setVehicleInfo(VehicleInfo vehicleInfo) {
        this.vehicleInfo = vehicleInfo;
    }

    public String getInstructorNote() {
        return instructorNote;
    }

    public void setInstructorNote(String instructorNote) {
        this.instructorNote = instructorNote;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }
}