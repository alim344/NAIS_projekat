package com.example.instructor_management.DTO;

import com.example.instructor_management.model.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO {
    private Long id;
    private String registrationNumber;
    private LocalDate registrationExpiryDate;
    private VehicleStatus status;
    private Integer currentMileage;
}
