package com.example.class_organization.model;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.time.LocalDate;
import java.time.LocalTime;

@Node("TimePreference")
public class TimePreference {

    @Id
    @GeneratedValue
    private Long id;

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

}
