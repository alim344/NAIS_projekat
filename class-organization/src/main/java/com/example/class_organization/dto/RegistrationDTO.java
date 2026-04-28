package com.example.class_organization.dto;

import com.example.class_organization.model.Category;
import com.example.class_organization.model.TrainingStatus;

import java.time.LocalDateTime;

public class RegistrationDTO {

    private String username;
    private String password;
    private String email;
    private String name;
    private String lastname;
    private LocalDateTime startOfTraining;
    private String preferredLocation;
    private boolean theoryCompleted;
    private Category category;
    private TrainingStatus status;

    public RegistrationDTO(String username, String password, TrainingStatus status, Category category, boolean theoryCompleted, String preferredLocation, LocalDateTime startOfTraining, String lastname, String name, String email) {
        this.username = username;
        this.password = password;
        this.status = status;
        this.category = category;
        this.theoryCompleted = theoryCompleted;
        this.preferredLocation = preferredLocation;
        this.startOfTraining = startOfTraining;
        this.lastname = lastname;
        this.name = name;
        this.email = email;
    }


    public RegistrationDTO() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public LocalDateTime getStartOfTraining() {
        return startOfTraining;
    }

    public void setStartOfTraining(LocalDateTime startOfTraining) {
        this.startOfTraining = startOfTraining;
    }

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
}
