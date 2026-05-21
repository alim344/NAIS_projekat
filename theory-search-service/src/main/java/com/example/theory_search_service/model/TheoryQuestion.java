package com.example.theory_search_service.model;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "theory-questions")
public class TheoryQuestion {

    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String questionText;

    @Field(type = FieldType.Keyword)
    private String correctAnswer;

    @Field(type = FieldType.Keyword)
    private String[] wrongAnswers;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String lessonTitle;

    @Field(type = FieldType.Integer)
    private int lessonOrderNumber;

    @Field(type = FieldType.Integer)
    private int difficultyLevel;

    @Field(type = FieldType.Keyword)
    private String category;
}
