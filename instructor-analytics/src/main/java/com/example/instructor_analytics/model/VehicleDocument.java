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

@Document(indexName = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDocument {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String registrationNumber;

    @Field(type = FieldType.Text)
    private String brand;

    @Field(type = FieldType.Keyword)
    private String status;

    @Field(type = FieldType.Integer)
    private Integer currentMileage;

    @Field(type = FieldType.Keyword)
    private String registrationExpiryDate;

    @Field(type = FieldType.Text)
    private String instructorName;

    @Field(type = FieldType.Text)
    private String instructorLastname;

}
