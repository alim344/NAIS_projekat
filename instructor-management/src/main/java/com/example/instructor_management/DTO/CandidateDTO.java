package com.example.instructor_management.DTO;


import com.example.instructor_management.model.Category;
import com.example.instructor_management.model.TrainingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateDTO {
    private String id;
    private String username;
    private String password;
    private String email;
    private String name;
    private String lastname;
    private boolean theoryCompleted;
    private Category category;
    private TrainingStatus trainingStatus;
}
