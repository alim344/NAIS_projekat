package com.example.class_organization.model;

import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
public class Teaching {

    @RelationshipId
    private Long id;

    @TargetNode
    private PracticalClass practicalClass;

    private String note;
    private int score;

    public Teaching(PracticalClass practicalClass, String note, int score) {
        this.practicalClass = practicalClass;
        this.note = note;
        this.score = score;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PracticalClass getPracticalClass() {
        return practicalClass;
    }

    public void setPracticalClass(PracticalClass practicalClass) {
        this.practicalClass = practicalClass;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
