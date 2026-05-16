package com.example.instructor_management.service;

import com.example.instructor_management.model.Vehicle;
import com.example.instructor_management.repository.VehicleRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    private final Neo4jClient neo4jClient;

    public List<Map<String, Object>> getVehiclesCountByStatus() {
        String query = "MATCH (v:Vehicle) " +
                "WITH v.status as status, COUNT(v) AS vehicleCount " +
                "WHERE status IS NOT NULL " +
                "RETURN status, vehicleCount " +
                "ORDER BY vehicleCount DESC";

        return new ArrayList<>(neo4jClient.query(query).fetch().all());
    }
}
