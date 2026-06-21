package com.example.instructor_management.saga;

import com.example.instructor_management.DTO.AssignInstructorRequest;
import com.example.instructor_management.model.Instructor;
import com.example.instructor_management.model.Vehicle;
import com.example.instructor_management.model.VehicleStatus;
import com.example.instructor_management.repository.InstructorRepository;
import com.example.instructor_management.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AssignVehicleSagaOrchestrator {

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${instructor.analytics.url}")
    private String analyticsServiceUrl;

    public boolean assignVehicleSaga(String instructorId, String vehicleRegistrationNumber, boolean simulateFail) {
        System.out.println("\n=== POKRETANJE SAGA - DODELA VOZILA INSTRUKTORU ===");
        System.out.println("Instructor ID: " + instructorId);
        System.out.println("Vehicle Registration: " + vehicleRegistrationNumber);

        Instructor instructor = instructorRepository.findById(instructorId).orElse(null);
        Vehicle vehicle = vehicleRepository.findByRegistrationNumber(vehicleRegistrationNumber).orElse(null);

        if (instructor == null || vehicle == null) {
            System.err.println("[SAGA ORKESTRATOR] Instruktor ili vozilo ne postoji.");
            return false;
        }

        if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
            System.err.println("[SAGA ORKESTRATOR] Vozilo nije dostupno. Status: " + vehicle.getStatus());
            return false;
        }

        // KORAK 1: Neo4j - dodela vozila instruktoru
        Vehicle previousVehicle = instructor.getVehicle();
        instructor.setVehicle(vehicle);
        instructorRepository.save(instructor);

        vehicle.setStatus(VehicleStatus.IN_USE);
        vehicleRepository.save(vehicle);

        System.out.println("[SAGA ORKESTRATOR] Korak 1 uspešan: Vozilo " + vehicleRegistrationNumber +
                " dodeljeno instruktoru " + instructorId);

        try {
            if (simulateFail) {
                throw new RuntimeException("Simulirani pad instructor-analytics servisa");
            }

            // KORAK 2: Elasticsearch - ažuriranje VehicleDocument
            String vehicleUrl = analyticsServiceUrl + "/api/vehicles/" + vehicleRegistrationNumber + "/assign-instructor";
            AssignInstructorRequest request = new AssignInstructorRequest(
                    instructor.getName(), instructor.getLastname()
            );
            restTemplate.exchange(vehicleUrl, HttpMethod.PUT, new HttpEntity<>(request), String.class);
            System.out.println("[SAGA ORKESTRATOR] Korak 2a uspešan: Elasticsearch vozilo ažurirano.");

            // KORAK 2: Elasticsearch - ažuriranje InstructorDocument
            String instructorSearchUrl = analyticsServiceUrl + "/api/instructors/by-email?email=" + instructor.getEmail();
            ResponseEntity<Map> response = restTemplate.getForEntity(instructorSearchUrl, Map.class);

            if (response.getBody() != null && response.getBody().containsKey("id")) {
                String esInstructorId = (String) response.getBody().get("id");
                String instructorUpdateUrl = analyticsServiceUrl + "/api/instructors/" + esInstructorId + "/assign-vehicle";

                Map<String, String> requestBody = new HashMap<>();
                requestBody.put("registrationNumber", vehicleRegistrationNumber);

                restTemplate.exchange(instructorUpdateUrl, HttpMethod.PUT,
                        new HttpEntity<>(requestBody), String.class);
                System.out.println("[SAGA ORKESTRATOR] Korak 2 uspešan: Elasticsearch instruktor ažuriran. Email: " + instructor.getEmail());
            } else {
                System.err.println("[SAGA ORKESTRATOR] Instruktor nije pronadjen u Elasticsearch-u! Email: " + instructor.getEmail());
            }

            System.out.println("=== SAGA USPEŠNO ZAVRŠENA ===");
            return true;

        } catch (Exception e) {
            System.err.println("[SAGA ORKESTRATOR] Korak 2 neuspešan: " + e.getMessage());
            System.err.println("[SAGA ORKESTRATOR] Korak 3: POKREĆEM KOMPENZACIONU TRANSAKCIJU...");

            // KOMPENZACIJA:
            instructor.setVehicle(previousVehicle);
            instructorRepository.save(instructor);

            // Vrati status vozila na AVAILABLE
            vehicle.setStatus(VehicleStatus.AVAILABLE);
            vehicleRepository.save(vehicle);

            System.err.println("[SAGA ORKESTRATOR] Kompenzacija uspešna: Vozilo vraćeno na prethodno stanje.");
            throw new RuntimeException("SAGA prekinuta: " + e.getMessage());
        }
    }

}
