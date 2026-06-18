package com.example.instructor_management.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignInstructorRequest {
    private String instructorName;
    private String instructorLastname;

    public AssignInstructorRequest(String instructorName, String instructorLastname) {
        this.instructorName = instructorName;
        this.instructorLastname = instructorLastname;
    }
}
