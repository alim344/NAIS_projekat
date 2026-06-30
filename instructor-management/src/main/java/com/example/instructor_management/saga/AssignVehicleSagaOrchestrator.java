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

        Instructor instructor = instructorRepository.findById(instructorId).orElse(null);
        Vehicle vehicle = vehicleRepository.findByRegistrationNumber(vehicleRegistrationNumber).orElse(null);

        if (instructor == null || vehicle == null) {
            System.err.println("[SAGA] Instruktor ili vozilo ne postoji.");
            return false;
        }

        if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
            System.err.println("[SAGA] Vozilo nije dostupno. Status: " + vehicle.getStatus());
            return false;
        }

        // =============================================
        // KORAK 1: Neo4j — promeni status vozila
        // =============================================
        vehicle.setStatus(VehicleStatus.IN_USE);
        vehicleRepository.save(vehicle);
        System.out.println("[SAGA] Korak 1 uspešan: Vehicle status -> IN_USE u Neo4j");

        try {
            if (simulateFail) {
                throw new RuntimeException("Simulirani pad Elasticsearch servisa");
            }

            // =============================================
            // KORAK 2: Elasticsearch — dodeli vozilo instruktoru
            // =============================================
            String searchUrl = analyticsServiceUrl + "/api/instructors/by-email?email=" + instructor.getEmail();
            ResponseEntity<Map> response = restTemplate.getForEntity(searchUrl, Map.class);

            if (response.getBody() == null || !response.getBody().containsKey("id")) {
                throw new RuntimeException("Instruktor nije pronadjen u Elasticsearch-u! Email: " + instructor.getEmail());
            }

            String esInstructorId = (String) response.getBody().get("id");
            String assignUrl = analyticsServiceUrl + "/api/instructors/" + esInstructorId + "/assign-vehicle";

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("registrationNumber", vehicleRegistrationNumber);
            restTemplate.exchange(assignUrl, HttpMethod.PUT, new HttpEntity<>(requestBody), String.class);

            System.out.println("[SAGA] Korak 2 uspešan: vehicleRegistrationNumber -> " + vehicleRegistrationNumber + " u Elasticsearch");
            System.out.println("=== SAGA USPEŠNO ZAVRŠENA ===");
            return true;

        } catch (Exception e) {
            // =============================================
            // KOMPENZACIJA: vrati Neo4j na prethodno stanje
            // =============================================
            System.err.println("[SAGA] Korak 2 neuspešan: " + e.getMessage());
            System.err.println("[SAGA] Pokrećem kompenzacionu transakciju...");

            vehicle.setStatus(VehicleStatus.AVAILABLE);
            vehicleRepository.save(vehicle);

            System.err.println("[SAGA] Kompenzacija uspešna: Vehicle status -> AVAILABLE u Neo4j");
            throw new RuntimeException("SAGA prekinuta: " + e.getMessage());
        }
    }
}