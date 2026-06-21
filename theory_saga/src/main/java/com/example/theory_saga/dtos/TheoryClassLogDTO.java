package com.example.theory_saga.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class TheoryClassLogDTO {
    private String id;
    private String professorUsername;
    private String professorFullName;
    private String classroomName;
    private int classroomCapacity;
    private String lessonTitle;
    private int lessonOrderNumber;
    private String category;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int durationMinutes;
    private int candidateCount;
    private float averageCandidateScore;
    private boolean fullyAttended;
}
