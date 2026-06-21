package com.example.instructor_management;

import com.example.instructor_management.model.*;
import com.example.instructor_management.repository.InstructorRepository;
import com.example.instructor_management.repository.VehicleRepository;
import com.example.instructor_management.repository.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("DataLoader za Neo4j started...");

        instructorRepository.deleteAll();
        vehicleRepository.deleteAll();
        candidateRepository.deleteAll();

        System.out.println("Stari podaci obrisani, unosimo nove u Neo4j...");

        loadInstructors();
        loadVehicles();

        System.out.println("Test podaci za Neo4j uspesno uneti!");
        printTestData();
    }

    private void loadInstructors() {
        // 1. Marko Petrović
        Instructor instructor1 = new Instructor();
        instructor1.setUsername("markop");
        instructor1.setPassword("password123");
        instructor1.setEmail("marko.petrovic@autoskola.rs");
        instructor1.setName("Marko");
        instructor1.setLastname("Petrović");
        instructor1.setMaxCapacity(5);
        instructor1.setVehicle(null);
        instructorRepository.save(instructor1);

        // 2. Ana Jovanović
        Instructor instructor2 = new Instructor();
        instructor2.setUsername("anaj");
        instructor2.setPassword("password123");
        instructor2.setEmail("ana.jovanovic@autoskola.rs");
        instructor2.setName("Ana");
        instructor2.setLastname("Jovanović");
        instructor2.setMaxCapacity(4);
        instructor2.setVehicle(null);
        instructorRepository.save(instructor2);

        // 3. Stefan Nikolić
        Instructor instructor3 = new Instructor();
        instructor3.setUsername("stefann");
        instructor3.setPassword("password123");
        instructor3.setEmail("stefan.nikolic@autoskola.rs");
        instructor3.setName("Stefan");
        instructor3.setLastname("Nikolić");
        instructor3.setMaxCapacity(6);
        instructor3.setVehicle(null);
        instructorRepository.save(instructor3);

        // 4. Jovana Marković
        Instructor instructor4 = new Instructor();
        instructor4.setUsername("jovanam");
        instructor4.setPassword("password123");
        instructor4.setEmail("jovana.markovic@autoskola.rs");
        instructor4.setName("Jovana");
        instructor4.setLastname("Marković");
        instructor4.setMaxCapacity(3);
        instructor4.setVehicle(null);
        instructorRepository.save(instructor4);

        // 5. Nikola Đorđević
        Instructor instructor5 = new Instructor();
        instructor5.setUsername("nikolad");
        instructor5.setPassword("password123");
        instructor5.setEmail("nikola.djordjevic@autoskola.rs");
        instructor5.setName("Nikola");
        instructor5.setLastname("Đorđević");
        instructor5.setMaxCapacity(7);
        instructor5.setVehicle(null);
        instructorRepository.save(instructor5);

        System.out.println("5 instruktora uneto u Neo4j (svi bez vozila).");
    }

    private void loadVehicles() {
        // 1. Volkswagen NS-123-AB
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setRegistrationNumber("NS-123-AB");
        vehicle1.setRegistrationExpiryDate(LocalDate.of(2027, 5, 15));
        vehicle1.setStatus(VehicleStatus.AVAILABLE);
        vehicle1.setCurrentMileage(45230);
        vehicleRepository.save(vehicle1);

        // 2. Renault BG-456-BC
        Vehicle vehicle2 = new Vehicle();
        vehicle2.setRegistrationNumber("BG-456-BC");
        vehicle2.setRegistrationExpiryDate(LocalDate.of(2026, 8, 20));
        vehicle2.setStatus(VehicleStatus.AVAILABLE);
        vehicle2.setCurrentMileage(38450);
        vehicleRepository.save(vehicle2);

        // 3. Fiat NI-789-CD
        Vehicle vehicle3 = new Vehicle();
        vehicle3.setRegistrationNumber("NI-789-CD");
        vehicle3.setRegistrationExpiryDate(LocalDate.of(2025, 12, 1));
        vehicle3.setStatus(VehicleStatus.AVAILABLE);
        vehicle3.setCurrentMileage(67210);
        vehicleRepository.save(vehicle3);

        // 4. Opel KG-321-DE
        Vehicle vehicle4 = new Vehicle();
        vehicle4.setRegistrationNumber("KG-321-DE");
        vehicle4.setRegistrationExpiryDate(LocalDate.of(2028, 3, 25));
        vehicle4.setStatus(VehicleStatus.AVAILABLE);
        vehicle4.setCurrentMileage(52380);
        vehicleRepository.save(vehicle4);

        // 5. Ford SU-654-EF
        Vehicle vehicle5 = new Vehicle();
        vehicle5.setRegistrationNumber("SU-654-EF");
        vehicle5.setRegistrationExpiryDate(LocalDate.of(2026, 10, 12));
        vehicle5.setStatus(VehicleStatus.AVAILABLE);
        vehicle5.setCurrentMileage(29870);
        vehicleRepository.save(vehicle5);

        System.out.println("5 vozila uneto u Neo4j (sva AVAILABLE).");
    }

    private void printTestData() {
        System.out.println("\n========== NEO4J TEST PODACI ==========");

        System.out.println("\n--- INSTRUKTORI (svi bez dodeljenih vozila) ---");
        Iterable<Instructor> instructors = instructorRepository.findAll();
        for (Instructor instructor : instructors) {
            System.out.println("- " + instructor.getName() + " " + instructor.getLastname() +
                    " | Email: " + instructor.getEmail() +
                    " | Kapacitet: " + instructor.getMaxCapacity() +
                    " | Vozilo: NEMA");
        }

        System.out.println("\n--- VOZILA (sva AVAILABLE, bez instruktora) ---");
        Iterable<Vehicle> vehicles = vehicleRepository.findAll();
        for (Vehicle vehicle : vehicles) {
            System.out.println("- " + vehicle.getRegistrationNumber() +
                    " | " + vehicle.getStatus() +
                    " | " + vehicle.getCurrentMileage() + " km");
        }

        System.out.println("\n===================================\n");
    }
}