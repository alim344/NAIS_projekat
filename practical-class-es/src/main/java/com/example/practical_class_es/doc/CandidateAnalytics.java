package com.example.practical_class_es.doc;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Document(indexName = "candidate-analytics-timeprefs")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CandidateAnalytics implements Serializable {

    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long candidateId;

    @Field(type = FieldType.Keyword)
    private String name;

    @Field(type = FieldType.Keyword)
    private String lastname;

    @Field(type = FieldType.Keyword)
    private String email;

    @Field(type = FieldType.Keyword)
    private String startOfTraining;

    @Field(type = FieldType.Keyword)
    private String preferredLocation;

    @Field(type = FieldType.Boolean)
    private boolean theoryCompleted;

    @Field(type = FieldType.Keyword)
    private String status;

    @Field(type = FieldType.Keyword)
    private String category;

    @Field(type = FieldType.Nested)
    private List<TimePrefInfo> activePrefs;

    @Field(type = FieldType.Integer)
    private Integer totalKmDriven;

    @Field(type = FieldType.Integer)
    private Integer numberOfHeldClasses;

    @Field(type = FieldType.Double)
    private Double avgClassGrade;

    public CandidateAnalytics() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }

    public List<TimePrefInfo> getActivePrefs() {
        return activePrefs;
    }

    public void setActivePrefs(List<TimePrefInfo> activePrefs) {
        this.activePrefs = activePrefs;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isTheoryCompleted() {
        return theoryCompleted;
    }

    public void setTheoryCompleted(boolean theoryCompleted) {
        this.theoryCompleted = theoryCompleted;
    }

    public String getPreferredLocation() {
        return preferredLocation;
    }

    public void setPreferredLocation(String preferredLocation) {
        this.preferredLocation = preferredLocation;
    }

    public String getStartOfTraining() {
        return startOfTraining;
    }

    public void setStartOfTraining(String startOfTraining) {
        this.startOfTraining = startOfTraining;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTotalKmDriven() {
        return totalKmDriven;
    }

    public void setTotalKmDriven(Integer totalKmDriven) {
        this.totalKmDriven = totalKmDriven;
    }

    public Integer getNumberOfHeldClasses() {
        return numberOfHeldClasses;
    }

    public void setNumberOfHeldClasses(Integer numberOfHeldClasses) {
        this.numberOfHeldClasses = numberOfHeldClasses;
    }

    public Double getAvgClassGrade() {
        return avgClassGrade;
    }

    public void setAvgClassGrade(Double avgClassGrade) {
        this.avgClassGrade = avgClassGrade;
    }
}