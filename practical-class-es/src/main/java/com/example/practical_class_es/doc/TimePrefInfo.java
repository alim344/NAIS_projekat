package com.example.practical_class_es.doc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TimePrefInfo implements Serializable {

    @Field(type = FieldType.Keyword)
    private String date;

    @Field(type = FieldType.Keyword)
    private String startTime;

    @Field(type = FieldType.Keyword)
    private String endTime;

    @Field(type = FieldType.Keyword)
    private String flexibility;

    public TimePrefInfo() {
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getFlexibility() {
        return flexibility;
    }

    public void setFlexibility(String flexibility) {
        this.flexibility = flexibility;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
