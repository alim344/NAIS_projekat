package com.example.instructor_analytics.controller;

import com.example.instructor_analytics.model.VehicleDocument;
import com.example.instructor_analytics.service.VehicleService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/vehicles")
@AllArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;


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

    @GetMapping("/search-by-brand")
    public ResponseEntity<Map<String, Object>> searchByBrand(
            @RequestParam String brand,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(vehicleService.searchVehiclesByBrandAndStatus(brand, status));
    }
}
