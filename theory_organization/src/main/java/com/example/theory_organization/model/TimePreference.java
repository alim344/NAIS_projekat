package com.example.theory_organization.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Node("TimePreference")
public class TimePreference {
    @Id
    @GeneratedValue
    private Long id;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

}
