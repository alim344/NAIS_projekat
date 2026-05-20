package com.example.instructor_analytics.service;

import com.example.instructor_analytics.model.VehicleDocument;
import com.example.instructor_analytics.repository.VehicleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@AllArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleDocument saveVehicle(VehicleDocument vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public Optional<VehicleDocument> getVehicleById(String id) {
        return vehicleRepository.findById(id);
    }

    public Iterable<VehicleDocument> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public VehicleDocument updateVehicle(String id, VehicleDocument updated) {
        updated.setId(id);
        return vehicleRepository.save(updated);
    }

    public void deleteVehicle(String id) {
        vehicleRepository.deleteById(id);
    }
}
