package com.example.class_organization.model;

import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
public class Attendance {

    @RelationshipId
    private Long id;

    @TargetNode
    private PracticalClass practicalClass;

    private boolean present;

    private Integer kmDriven;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getKmDriven() {
        return kmDriven;
    }

    public void setKmDriven(Integer kmDriven) {
        this.kmDriven = kmDriven;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    public PracticalClass getPracticalClass() {
        return practicalClass;
    }

    public void setPracticalClass(PracticalClass practicalClass) {
        this.practicalClass = practicalClass;
    }
}
