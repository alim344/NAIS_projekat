package com.example.instructor_analytics.model;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum Category {
    A, A1, A2, B, BE, C, CE, D, DE
}
