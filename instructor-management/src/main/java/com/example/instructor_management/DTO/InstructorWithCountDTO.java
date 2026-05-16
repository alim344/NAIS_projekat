package com.example.instructor_management.DTO;

import com.example.instructor_management.model.Instructor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstructorWithCountDTO {
    private String id;
    private String name;
    private String lastname;
    private Long candidateCount;
}
