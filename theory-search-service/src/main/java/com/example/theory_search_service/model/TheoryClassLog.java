package com.example.theory_search_service.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "theory-class-log")
public class TheoryClassLog {

    @Id
    private Long id;

    @Field(type = FieldType.Keyword)
    private String professorUsername;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String professorFullName;

    @Field(type = FieldType.Keyword)
    private String classroomName;

    @Field(type = FieldType.Integer)
    private int classroomCapacity;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String lessonTitle;

    @Field(type = FieldType.Integer)
    private int lessonOrderNumber;

    @Field(type = FieldType.Keyword)
    private String category;

    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;

    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;

    @Field(type = FieldType.Integer)
    private int durationMinutes;

    @Field(type = FieldType.Integer)
    private int candidateCount;

    @Field(type = FieldType.Float)
    private float averageCandidateScore;

    @Field(type = FieldType.Boolean)
    private boolean fullyAttended;
}
