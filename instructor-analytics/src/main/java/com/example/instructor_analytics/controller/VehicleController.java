package com.example.instructor_analytics.controller;

import com.example.instructor_analytics.model.VehicleDocument;
import com.example.instructor_analytics.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<VehicleDocument> save(@RequestBody VehicleDocument vehicle) {
        return new ResponseEntity<>(vehicleService.saveVehicle(vehicle), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleDocument> getById(@PathVariable String id) {
        Optional<VehicleDocument> vehicle = vehicleService.getVehicleById(id);
        return vehicle.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Iterable<VehicleDocument>> getAll() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleDocument> update(@PathVariable String id,
                                                  @RequestBody VehicleDocument vehicle) {
        return ResponseEntity.ok(vehicleService.updateVehicle(id, vehicle));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/expiring-registration")
    public Map<String, Object> getVehiclesWithExpiringRegistration(
            @RequestParam int daysAhead,
            @RequestParam(required = false) String status) {

        return vehicleService.findVehiclesWithExpiringRegistration(daysAhead, status);
    }

    @GetMapping("/statistics/by-brand")
    public Map<String, Object> getVehicleStatisticsByBrand(
            @RequestParam String brand,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer minMileage) {

        return vehicleService.getVehicleStatisticsByBrand(brand, status, minMileage);
    }

}
