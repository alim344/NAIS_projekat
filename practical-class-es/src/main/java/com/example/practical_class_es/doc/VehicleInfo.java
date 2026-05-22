package com.example.practical_class_es.doc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VehicleInfo implements Serializable {

    private Long id;
    private String model;
    private String registration;
    private boolean malfunction;

    public VehicleInfo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
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
}
