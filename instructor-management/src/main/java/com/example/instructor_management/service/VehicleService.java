package com.example.instructor_management.service;

import com.example.instructor_management.model.Vehicle;
import com.example.instructor_management.repository.VehicleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class VehicleService {
    private final VehicleRepository vehicleRepository;

    public Vehicle createVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Vehicle getVehicleById(String id) {
        return vehicleRepository.findById(id).orElse(null);
    }

    public Vehicle updateVehicle(String id, Vehicle updatedVehicle) {
        Vehicle existing = vehicleRepository.findById(id).orElse(null);
        if (existing == null) return null;

        existing.setRegistrationNumber(updatedVehicle.getRegistrationNumber());
        existing.setRegistrationExpiryDate(updatedVehicle.getRegistrationExpiryDate());
        existing.setStatus(updatedVehicle.getStatus());
        existing.setCurrentMileage(updatedVehicle.getCurrentMileage());

        return vehicleRepository.save(existing);
    }

    public void deleteVehicle(String id) {
        vehicleRepository.deleteById(id);
    }
}
