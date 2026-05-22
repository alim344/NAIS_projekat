package com.example.instructor_analytics.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.util.List;

@Document(indexName = "instructors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InstructorDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Text)
    private String lastName;

    @Field(type = FieldType.Keyword)
    private String email;

    @Field(type = FieldType.Integer)
    private Integer maxCapacity;

    @Field(type = FieldType.Integer)
    private Integer currentCandidateCount;

    @Field(type = FieldType.Keyword)
    private String vehicleRegistrationNumber;

    @Field(type = FieldType.Text)
    private String documentTypes;

    @Field(type = FieldType.Date, format = DateFormat.basic_date)  // yyyy-MM-dd
    private LocalDate licenseExpiryDate;

    @Field(type = FieldType.Keyword)
    private List<Category> categories;
}
