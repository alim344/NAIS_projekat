package com.example.class_organization.dto;

public class TeachingDTO {

    private String username;
    private String classId;
    private String note;
    private int score;
    public TeachingDTO() {}
    public TeachingDTO(String username, String classId, String note, int score) {
        this.username = username;
        this.classId = classId;
        this.note = note;
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
