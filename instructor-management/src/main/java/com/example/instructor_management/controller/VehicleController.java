package com.example.instructor_management.controller;


import com.example.instructor_management.DTO.VehicleDTO;
import com.example.instructor_management.model.Vehicle;
import com.example.instructor_management.saga.AssignVehicleSagaOrchestrator;
import com.example.instructor_management.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
public class VehicleController {
    private final VehicleService vehicleService;

    private final AssignVehicleSagaOrchestrator sagaOrchestrator;

    @GetMapping
    public List<Vehicle> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable String id) {
        Vehicle vehicle = vehicleService.getVehicleById(id);
        return vehicle != null ? ResponseEntity.ok(vehicle) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public  ResponseEntity<String> createVehicle(@RequestBody VehicleDTO createDTO) {
        Vehicle vehicle = convertToEntity(createDTO);
        vehicleService.createVehicle(vehicle);
        return ResponseEntity.ok("saved");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable String id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateVehicle(@PathVariable String id, @RequestBody VehicleDTO updateDTO) {
        Vehicle existingVehicle = vehicleService.getVehicleById(id);
        if (existingVehicle == null) {
            return ResponseEntity.notFound().build();
        }

        existingVehicle.setRegistrationNumber(updateDTO.getRegistrationNumber());
        existingVehicle.setRegistrationExpiryDate(updateDTO.getRegistrationExpiryDate());
        existingVehicle.setStatus(updateDTO.getStatus());
        existingVehicle.setCurrentMileage(updateDTO.getCurrentMileage());

        vehicleService.createVehicle(existingVehicle);
        return ResponseEntity.ok("successful");
    }

    public Vehicle convertToEntity(VehicleDTO dto) {
        Vehicle vehicle = new Vehicle();
        vehicle.setRegistrationNumber(dto.getRegistrationNumber());
        vehicle.setRegistrationExpiryDate(dto.getRegistrationExpiryDate());
        vehicle.setStatus(dto.getStatus());
        vehicle.setCurrentMileage(dto.getCurrentMileage());
        return vehicle;
    }

    @GetMapping("/by-status")
    public List<Map<String, Object>> getVehiclesCountByStatus() {
        return vehicleService.getVehiclesCountByStatus();
    }

    @PostMapping("/{instructorId}/vehicles/{registrationNumber}/saga")
    public ResponseEntity<String> assignVehicleViaSaga(
            @PathVariable String instructorId,
            @PathVariable String registrationNumber,
            @RequestParam(defaultValue = "false") boolean simulateFail) {
        boolean result = sagaOrchestrator.assignVehicleSaga(
                instructorId, registrationNumber, simulateFail);
        return result ? ResponseEntity.ok("Saga uspešna")
                : ResponseEntity.badRequest().body("Saga neuspešna");
    }
}
