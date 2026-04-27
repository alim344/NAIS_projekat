package com.example.class_organization.model;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.time.LocalDate;

@Node("vehicle")
public class Vehicle {

    @Id
    @GeneratedValue
    private Long id;

    private String registrationNumber;

    private LocalDate registrationExpiryDate;


    private VehicleStatus status;

    private Integer currentMileage;


}
