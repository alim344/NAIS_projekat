package com.example.instructor_management.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstructorDTO {

    private Long id;
    private String username;
    private String email;
    private String password;
    private String name;
    private String lastname;
    private Integer maxCapacity;
}
