package com.example.class_organization.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class InstructorDTO {


    private String username;
    @JsonIgnore
    private String password;
    private String email;
    private String name;
    private String lastname;
    private Integer maxCapacity;

    public InstructorDTO() {}

    public InstructorDTO( String username, String password, String email, String name, String lastname, Integer maxCapacity) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
        this.lastname = lastname;
        this.maxCapacity = maxCapacity;
    }



    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
}
