package com.example.theory_saga.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTheoryClassDTO {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long lessonId;
    private Long hallId;
    private Long professorId;
    private String professorUsername;
    private String professorFullName;
    private String classroomName;
    private int classroomCapacity;
    private String lessonTitle;
    private int lessonOrderNumber;
    private String category;
}
