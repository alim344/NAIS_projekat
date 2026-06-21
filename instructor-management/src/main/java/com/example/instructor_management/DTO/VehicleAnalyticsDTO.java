package com.example.instructor_management.DTO;

public class VehicleAnalyticsDTO {
    private String id;
    private String registrationNumber;
    private String brand;
    private String status;
    private Integer currentMileage;
    private String registrationExpiryDate;
    private String instructorName;
    private String instructorLastname;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getCurrentMileage() { return currentMileage; }
    public void setCurrentMileage(Integer currentMileage) { this.currentMileage = currentMileage; }
    public String getRegistrationExpiryDate() { return registrationExpiryDate; }
    public void setRegistrationExpiryDate(String registrationExpiryDate) { this.registrationExpiryDate = registrationExpiryDate; }
    public String getInstructorName() { return instructorName; }
    public void setInstructorName(String instructorName) { this.instructorName = instructorName; }
    public String getInstructorLastname() { return instructorLastname; }
    public void setInstructorLastname(String instructorLastname) { this.instructorLastname = instructorLastname; }
}
