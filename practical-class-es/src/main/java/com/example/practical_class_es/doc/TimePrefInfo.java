package com.example.practical_class_es.doc;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

public class TimePrefInfo {

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
