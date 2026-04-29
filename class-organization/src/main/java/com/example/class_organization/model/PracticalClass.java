package com.example.class_organization.model;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Node("PracticalClass")
public class PracticalClass {


    @Id
    @GeneratedValue
    private Long id;

    private String startTime;

    private String endTime;

    private boolean completed;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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


    public LocalDateTime getStartTimeAsDateTime() {
        if (this.startTime == null) return null;
        return LocalDateTime.parse(this.startTime.replace("Z", ""));
    }

    public void setStartTimeFromDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            this.startTime = null;
        } else {
            this.startTime = dateTime.toString();
        }
    }

    public LocalDateTime getEndTimeAsDateTime() {
        if (this.endTime == null) return null;
        return LocalDateTime.parse(this.endTime.replace("Z", ""));
    }

    public void setEndTimeFromDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            this.endTime = null;
        } else {
            this.endTime = dateTime.toString();
        }
    }
}
