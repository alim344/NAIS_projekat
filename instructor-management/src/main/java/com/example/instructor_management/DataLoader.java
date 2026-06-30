package com.example.instructor_management;

import com.example.instructor_management.model.*;
import com.example.instructor_management.repository.InstructorRepository;
import com.example.instructor_management.repository.VehicleRepository;
import com.example.instructor_management.repository.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

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

        loadVehicles();
        loadInstructors();

        System.out.println("Test podaci za Neo4j uspesno uneti!");
        printTestData();
    }

    private void loadInstructors() {

        // ---- KANDIDATI ----
        Candidate c1 = new Candidate();
        c1.setUsername("petar_k"); c1.setPassword("pass"); c1.setEmail("petar.k@mail.com");
        c1.setName("Petar"); c1.setLastname("Ković");
        c1.setTheoryCompleted(true); c1.setCategory(Category.B); c1.setTrainingStatus(TrainingStatus.PRACTICAL);
        candidateRepository.save(c1);

        Candidate c2 = new Candidate();
        c2.setUsername("milica_j"); c2.setPassword("pass"); c2.setEmail("milica.j@mail.com");
        c2.setName("Milica"); c2.setLastname("Janković");
        c2.setTheoryCompleted(false); c2.setCategory(Category.B); c2.setTrainingStatus(TrainingStatus.THEORY);
        candidateRepository.save(c2);

        Candidate c3 = new Candidate();
        c3.setUsername("lazar_m"); c3.setPassword("pass"); c3.setEmail("lazar.m@mail.com");
        c3.setName("Lazar"); c3.setLastname("Mitić");
        c3.setTheoryCompleted(true); c3.setCategory(Category.B); c3.setTrainingStatus(TrainingStatus.PASSED);
        candidateRepository.save(c3);

        Candidate c4 = new Candidate();
        c4.setUsername("ivana_p"); c4.setPassword("pass"); c4.setEmail("ivana.p@mail.com");
        c4.setName("Ivana"); c4.setLastname("Popović");
        c4.setTheoryCompleted(false); c4.setCategory(Category.A); c4.setTrainingStatus(TrainingStatus.PENDING);
        candidateRepository.save(c4);

        Candidate c5 = new Candidate();
        c5.setUsername("djordje_v"); c5.setPassword("pass"); c5.setEmail("djordje.v@mail.com");
        c5.setName("Đorđe"); c5.setLastname("Vasić");
        c5.setTheoryCompleted(true); c5.setCategory(Category.B); c5.setTrainingStatus(TrainingStatus.PRACTICAL);
        candidateRepository.save(c5);

        Candidate c6 = new Candidate();
        c6.setUsername("maja_s"); c6.setPassword("pass"); c6.setEmail("maja.s@mail.com");
        c6.setName("Maja"); c6.setLastname("Stanković");
        c6.setTheoryCompleted(false); c6.setCategory(Category.B); c6.setTrainingStatus(TrainingStatus.THEORY);
        candidateRepository.save(c6);

        Candidate c7 = new Candidate();
        c7.setUsername("filip_r"); c7.setPassword("pass"); c7.setEmail("filip.r@mail.com");
        c7.setName("Filip"); c7.setLastname("Ristić");
        c7.setTheoryCompleted(true); c7.setCategory(Category.C); c7.setTrainingStatus(TrainingStatus.PASSED);
        candidateRepository.save(c7);

        // ---- DOKUMENTI ----
        InstructorDocuments doc1 = new InstructorDocuments();
        doc1.setDocumentType("INSTRUKTORSKA LICENCA B"); doc1.setExpiryDate(LocalDate.of(2027, 12, 31));

        InstructorDocuments doc2 = new InstructorDocuments();
        doc2.setDocumentType("SERTIFIKAT PRVA POMOC"); doc2.setExpiryDate(LocalDate.of(2026, 7, 10));

        InstructorDocuments doc3 = new InstructorDocuments();
        doc3.setDocumentType("INSTRUKTORSKA LICENCA C"); doc3.setExpiryDate(LocalDate.of(2025, 3, 20));

        InstructorDocuments doc4 = new InstructorDocuments();
        doc4.setDocumentType("VOZACKA DOZVOLA D"); doc4.setExpiryDate(LocalDate.of(2028, 9, 10));

        InstructorDocuments doc5 = new InstructorDocuments();
        doc5.setDocumentType("INSTRUKTORSKA LICENCA A"); doc5.setExpiryDate(LocalDate.of(2026, 11, 5));

        InstructorDocuments doc6 = new InstructorDocuments();
        // Ovo isce za ~20 dana - da se vidi u izvestaju za dokumente koji isticu
        doc6.setDocumentType("SERTIFIKAT ZA NOCNU VOZNJU"); doc6.setExpiryDate(LocalDate.now().plusDays(18));

        // ---- VOZILA (dohvatamo postojeca) ----
        Vehicle vNS = vehicleRepository.findByRegistrationNumber("NS-123-AB").orElse(null);
        Vehicle vBG = vehicleRepository.findByRegistrationNumber("BG-456-BC").orElse(null);
        Vehicle vKG = vehicleRepository.findByRegistrationNumber("KG-321-DE").orElse(null);

        // ---- INSTRUKTORI ----

        // 1. Marko - ima vozilo NS-123-AB, 2 kandidata, 1 dokument
        Instructor instructor1 = new Instructor();
        instructor1.setUsername("markop"); instructor1.setPassword("password123");
        instructor1.setEmail("marko.petrovic@autoskola.rs");
        instructor1.setName("Marko"); instructor1.setLastname("Petrović");
        instructor1.setMaxCapacity(5);
        instructor1.setVehicle(vNS);
        instructor1.setCandidates(Arrays.asList(c1, c2));
        instructor1.setDocuments(Arrays.asList(doc1));
        instructorRepository.save(instructor1);

        // 2. Ana - ima vozilo BG-456-BC, 2 kandidata, 2 dokumenta (jedan isce uskoro)
        Instructor instructor2 = new Instructor();
        instructor2.setUsername("anaj"); instructor2.setPassword("password123");
        instructor2.setEmail("ana.jovanovic@autoskola.rs");
        instructor2.setName("Ana"); instructor2.setLastname("Jovanović");
        instructor2.setMaxCapacity(4);
        instructor2.setVehicle(vBG);
        instructor2.setCandidates(Arrays.asList(c3, c4));
        instructor2.setDocuments(Arrays.asList(doc2, doc6));
        instructorRepository.save(instructor2);

        // 3. Stefan - bez vozila, 2 kandidata, 1 dokument
        Instructor instructor3 = new Instructor();
        instructor3.setUsername("stefann"); instructor3.setPassword("password123");
        instructor3.setEmail("stefan.nikolic@autoskola.rs");
        instructor3.setName("Stefan"); instructor3.setLastname("Nikolić");
        instructor3.setMaxCapacity(6);
        instructor3.setVehicle(null);
        instructor3.setCandidates(Arrays.asList(c5, c6));
        instructor3.setDocuments(Arrays.asList(doc3));
        instructorRepository.save(instructor3);

        // 4. Jovana - ima vozilo KG-321-DE, 1 kandidat, 1 dokument
        Instructor instructor4 = new Instructor();
        instructor4.setUsername("jovanam"); instructor4.setPassword("password123");
        instructor4.setEmail("jovana.markovic@autoskola.rs");
        instructor4.setName("Jovana"); instructor4.setLastname("Marković");
        instructor4.setMaxCapacity(3);
        instructor4.setVehicle(vKG);
        instructor4.setCandidates(Arrays.asList(c7));
        instructor4.setDocuments(Arrays.asList(doc4));
        instructorRepository.save(instructor4);

        // 5. Nikola - bez vozila, bez kandidata, 1 dokument
        Instructor instructor5 = new Instructor();
        instructor5.setUsername("nikolad"); instructor5.setPassword("password123");
        instructor5.setEmail("nikola.djordjevic@autoskola.rs");
        instructor5.setName("Nikola"); instructor5.setLastname("Đorđević");
        instructor5.setMaxCapacity(7);
        instructor5.setVehicle(null);
        instructor5.setCandidates(new ArrayList<>());
        instructor5.setDocuments(Arrays.asList(doc5));
        instructorRepository.save(instructor5);

        System.out.println("5 instruktora uneto u Neo4j (3 sa vozilima, kandidatima i dokumentima).");
    }
    private void loadVehicles() {
        // 1. Volkswagen NS-123-AB
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setRegistrationNumber("NS-123-AB");
        vehicle1.setRegistrationExpiryDate(LocalDate.of(2027, 5, 15));
        vehicle1.setStatus(VehicleStatus.IN_USE);
        vehicle1.setCurrentMileage(45230);
        vehicleRepository.save(vehicle1);

        // 2. Renault BG-456-BC
        Vehicle vehicle2 = new Vehicle();
        vehicle2.setRegistrationNumber("BG-456-BC");
        vehicle2.setRegistrationExpiryDate(LocalDate.of(2026, 8, 20));
        vehicle2.setStatus(VehicleStatus.IN_USE);
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
        vehicle4.setStatus(VehicleStatus.IN_USE);
        vehicle4.setCurrentMileage(52380);
        vehicleRepository.save(vehicle4);

        // 5. Ford SU-654-EF
        Vehicle vehicle5 = new Vehicle();
        vehicle5.setRegistrationNumber("SU-654-EF");
        vehicle5.setRegistrationExpiryDate(LocalDate.of(2026, 10, 12));
        vehicle5.setStatus(VehicleStatus.AVAILABLE);
        vehicle5.setCurrentMileage(29870);
        vehicleRepository.save(vehicle5);

        // 4. Opel KG-321-DE
        Vehicle vehicle6 = new Vehicle();
        vehicle6.setRegistrationNumber("NS-321-DE");
        vehicle6.setRegistrationExpiryDate(LocalDate.of(2028, 3, 25));
        vehicle6.setStatus(VehicleStatus.OUT_OF_SERVICE);
        vehicle6.setCurrentMileage(6000);
        vehicleRepository.save(vehicle6);

        // 5. Ford SU-654-EF
        Vehicle vehicle7 = new Vehicle();
        vehicle7.setRegistrationNumber("IN-654-EF");
        vehicle7.setRegistrationExpiryDate(LocalDate.of(2026, 10, 12));
        vehicle7.setStatus(VehicleStatus.OUT_OF_SERVICE);
        vehicle7.setCurrentMileage(70870);
        vehicleRepository.save(vehicle7);

        System.out.println("5 vozila uneto u Neo4j (sva AVAILABLE).");
    }

    private void printTestData() {
        System.out.println("\n========== NEO4J TEST PODACI ==========");

        System.out.println("\n--- INSTRUKTORI ---");
        for (Instructor instructor : instructorRepository.findAll()) {
            System.out.println("- " + instructor.getName() + " " + instructor.getLastname() +
                    " | Vozilo: " + (instructor.getVehicle() != null ? instructor.getVehicle().getRegistrationNumber() : "NEMA") +
                    " | Kandidati: " + instructor.getCandidates().size() +
                    " | Dokumenti: " + instructor.getDocuments().size());
        }

        System.out.println("\n--- VOZILA ---");
        for (Vehicle vehicle : vehicleRepository.findAll()) {
            System.out.println("- " + vehicle.getRegistrationNumber() +
                    " | " + vehicle.getStatus() +
                    " | " + vehicle.getCurrentMileage() + " km");
        }

        System.out.println("\n--- KANDIDATI ---");
        for (Candidate candidate : candidateRepository.findAll()) {
            System.out.println("- " + candidate.getName() + " " + candidate.getLastname() +
                    " | Status: " + candidate.getTrainingStatus() +
                    " | Kategorija: " + candidate.getCategory());
        }

        System.out.println("\n===================================\n");
    }
}