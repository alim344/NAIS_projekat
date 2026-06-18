package com.example.instructor_analytics.DTO;

public class AssignInstructorRequest {
    private String instructorName;
    private String instructorLastname;

    public AssignInstructorRequest() {}

    public AssignInstructorRequest(String instructorName, String instructorLastname) {
        this.instructorName = instructorName;
        this.instructorLastname = instructorLastname;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public String getInstructorLastname() {
        return instructorLastname;
    }

    public void setInstructorLastname(String instructorLastname) {
        this.instructorLastname = instructorLastname;
    }
}
