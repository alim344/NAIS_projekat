package com.example.instructor_management.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Node("User")
public class User {

    @Id
    @GeneratedValue
    private String id;

    private String username;

    @JsonIgnore
    private String password;

    private String email;

    private String name;

    private String lastname;
}
