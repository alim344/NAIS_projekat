package com.example.instructor_management.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstructorDocumentsDTO {
    private String id;
    private String documentType;
    private LocalDate expiryDate;
}
