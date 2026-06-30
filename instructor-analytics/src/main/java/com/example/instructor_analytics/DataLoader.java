package com.example.instructor_analytics;

import com.example.instructor_analytics.model.*;
import com.example.instructor_analytics.service.InstructorService;
import com.example.instructor_analytics.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private InstructorService instructorService;

    @Autowired
    private VehicleService vehicleService;

    private final Random random = new Random();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public void run(String... args) throws Exception {
        System.out.println("DataLoader started...");

        instructorService.deleteAll();
        vehicleService.deleteAll();

        System.out.println("Stari podaci obrisani, unosimo nove...");

       loadInstructors();
       loadVehicles();

        System.out.println("Test podaci uspesno uneti!");
    }

    private void loadInstructors() {
        InstructorDocument instructor1 = new InstructorDocument();
        instructor1.setId(UUID.randomUUID().toString());
        instructor1.setName("Marko");
        instructor1.setLastName("Petrović");
        instructor1.setEmail("marko.petrovic@autoskola.rs");
        instructor1.setMaxCapacity(5);
        instructor1.setCurrentCandidateCount(3);
        instructor1.setVehicleRegistrationNumber(null);
        instructor1.setDocumentTypes("INSTRUKTORSKA LICENCA B KATEGORIJE");
        instructor1.setLicenseExpiryDate(LocalDate.of(2027, 12, 31));
        instructor1.setCategories(Arrays.asList(Category.B, Category.BE));
        instructorService.saveInstructor(instructor1);

        InstructorDocument instructor2 = new InstructorDocument();
        instructor2.setId(UUID.randomUUID().toString());
        instructor2.setName("Ana");
        instructor2.setLastName("Jovanović");
        instructor2.setEmail("ana.jovanovic@autoskola.rs");
        instructor2.setMaxCapacity(4);
        instructor2.setCurrentCandidateCount(2);
        instructor2.setVehicleRegistrationNumber(null);
        instructor2.setDocumentTypes("DIPLOMA ZA INSTRUKTORA, SERTIFIKAT ZA PRVU POMOĆ");
        instructor2.setLicenseExpiryDate(LocalDate.of(2026, 6, 15));
        instructor2.setCategories(Arrays.asList(Category.B, Category.A));
        instructorService.saveInstructor(instructor2);

        InstructorDocument instructor3 = new InstructorDocument();
        instructor3.setId(UUID.randomUUID().toString());
        instructor3.setName("Stefan");
        instructor3.setLastName("Nikolić");
        instructor3.setEmail("stefan.nikolic@autoskola.rs");
        instructor3.setMaxCapacity(6);
        instructor3.setCurrentCandidateCount(4);
        instructor3.setVehicleRegistrationNumber(null);
        instructor3.setDocumentTypes("SERTIFIKAT ZA KAMIONE, DIPLOMA ZA INSTRUKTORA");
        instructor3.setLicenseExpiryDate(LocalDate.of(2025, 3, 20));
        instructor3.setCategories(Arrays.asList(Category.C, Category.CE, Category.BE));
        instructorService.saveInstructor(instructor3);

        InstructorDocument instructor4 = new InstructorDocument();
        instructor4.setId(UUID.randomUUID().toString());
        instructor4.setName("Jovana");
        instructor4.setLastName("Marković");
        instructor4.setEmail("jovana.markovic@autoskola.rs");
        instructor4.setMaxCapacity(3);
        instructor4.setCurrentCandidateCount(1);
        instructor4.setVehicleRegistrationNumber(null);
        instructor4.setDocumentTypes("INSTRUKTORSKA LICENCA, SERTIFIKAT ZA VOŽNJU U NOĆNIM USLOVIMA");
        instructor4.setLicenseExpiryDate(LocalDate.of(2028, 9, 10));
        instructor4.setCategories(Arrays.asList(Category.D, Category.DE, Category.B));
        instructorService.saveInstructor(instructor4);

        InstructorDocument instructor5 = new InstructorDocument();
        instructor5.setId(UUID.randomUUID().toString());
        instructor5.setName("Nikola");
        instructor5.setLastName("Đorđević");
        instructor5.setEmail("nikola.djordjevic@autoskola.rs");
        instructor5.setMaxCapacity(7);
        instructor5.setCurrentCandidateCount(5);
        instructor5.setVehicleRegistrationNumber(null);
        instructor5.setDocumentTypes("DIPLOMA ZA INSTRUKTORA, SERTIFIKAT ZA PRVU POMOĆ, VOZAČKA DOZVOLA D KATEGORIJE");
        instructor5.setLicenseExpiryDate(LocalDate.of(2026, 11, 5));
        instructor5.setCategories(Arrays.asList(Category.A, Category.A1, Category.A2));
        instructorService.saveInstructor(instructor5);
        
    }

    private void loadVehicles() {
        VehicleDocument vehicle1 = new VehicleDocument();
        vehicle1.setId(UUID.randomUUID().toString());
        vehicle1.setRegistrationNumber("NS-123-AB");
        vehicle1.setBrand("Volkswagen");
        vehicle1.setStatus("AVAILABLE");
        vehicle1.setCurrentMileage(45230);
        vehicle1.setRegistrationExpiryDate(LocalDate.of(2027, 5, 15).format(dateFormatter));
        vehicle1.setInstructorName(null);
        vehicle1.setInstructorLastname(null);
        vehicleService.saveVehicle(vehicle1);

        VehicleDocument vehicle2 = new VehicleDocument();
        vehicle2.setId(UUID.randomUUID().toString());
        vehicle2.setRegistrationNumber("BG-456-BC");
        vehicle2.setBrand("Renault");
        vehicle2.setStatus("AVAILABLE");
        vehicle2.setCurrentMileage(38450);
        vehicle2.setRegistrationExpiryDate(LocalDate.of(2026, 8, 20).format(dateFormatter));
        vehicle2.setInstructorName(null);
        vehicle2.setInstructorLastname(null);
        vehicleService.saveVehicle(vehicle2);

        VehicleDocument vehicle3 = new VehicleDocument();
        vehicle3.setId(UUID.randomUUID().toString());
        vehicle3.setRegistrationNumber("NI-789-CD");
        vehicle3.setBrand("Fiat");
        vehicle3.setStatus("AVAILABLE");
        vehicle3.setCurrentMileage(67210);
        vehicle3.setRegistrationExpiryDate(LocalDate.of(2025, 12, 1).format(dateFormatter));
        vehicle3.setInstructorName(null);
        vehicle3.setInstructorLastname(null);
        vehicleService.saveVehicle(vehicle3);

        VehicleDocument vehicle4 = new VehicleDocument();
        vehicle4.setId(UUID.randomUUID().toString());
        vehicle4.setRegistrationNumber("KG-321-DE");
        vehicle4.setBrand("Opel");
        vehicle4.setStatus("AVAILABLE");
        vehicle4.setCurrentMileage(52380);
        vehicle4.setRegistrationExpiryDate(LocalDate.of(2028, 3, 25).format(dateFormatter));
        vehicle4.setInstructorName(null);
        vehicle4.setInstructorLastname(null);
        vehicleService.saveVehicle(vehicle4);

        VehicleDocument vehicle5 = new VehicleDocument();
        vehicle5.setId(UUID.randomUUID().toString());
        vehicle5.setRegistrationNumber("SU-654-EF");
        vehicle5.setBrand("Ford");
        vehicle5.setStatus("AVAILABLE");
        vehicle5.setCurrentMileage(29870);
        vehicle5.setRegistrationExpiryDate(LocalDate.of(2026, 10, 12).format(dateFormatter));
        vehicle5.setInstructorName(null);
        vehicle5.setInstructorLastname(null);
        vehicleService.saveVehicle(vehicle5);

        System.out.println("5 vozila uneto (sva AVAILABLE, bez instruktora).");

        VehicleDocument vehicle6 = new VehicleDocument();
        vehicle6.setId(UUID.randomUUID().toString());
        vehicle6.setRegistrationNumber("IN-777-ET");
        vehicle6.setBrand("Ford");
        vehicle6.setStatus("OUT_OF_SERVICE");
        vehicle6.setCurrentMileage(20870);
        vehicle6.setRegistrationExpiryDate(LocalDate.of(2027, 10, 12).format(dateFormatter));
        vehicle6.setInstructorName(null);
        vehicle6.setInstructorLastname(null);
        vehicleService.saveVehicle(vehicle6);

        VehicleDocument vehicle7 = new VehicleDocument();
        vehicle7.setId(UUID.randomUUID().toString());
        vehicle7.setRegistrationNumber("IN-850-TA");
        vehicle7.setBrand("BMW");
        vehicle7.setStatus("OUT_OF_SERVICE");
        vehicle7.setCurrentMileage(30780);
        vehicle7.setRegistrationExpiryDate(LocalDate.of(2026, 10, 10).format(dateFormatter));
        vehicle7.setInstructorName(null);
        vehicle7.setInstructorLastname(null);
        vehicleService.saveVehicle(vehicle7);
    }

    private void printTestData() {
        System.out.println("\n========== TEST PODACI ==========");
        System.out.println("\n--- INSTRUKTORI (svi bez dodeljenih vozila) ---");
        System.out.println("1. Marko Petrović | Email: marko.petrovic@autoskola.rs | Kapacitet: 5/3 | Kategorije: B, BE | Vozilo: NEMA | Licenca ističe: 2027-12-31");
        System.out.println("2. Ana Jovanović | Email: ana.jovanovic@autoskola.rs | Kapacitet: 4/2 | Kategorije: B, A | Vozilo: NEMA | Licenca ističe: 2026-06-15");
        System.out.println("3. Stefan Nikolić | Email: stefan.nikolic@autoskola.rs | Kapacitet: 6/4 | Kategorije: C, CE, BE | Vozilo: NEMA | Licenca ističe: 2025-03-20");
        System.out.println("4. Jovana Marković | Email: jovana.markovic@autoskola.rs | Kapacitet: 3/1 | Kategorije: D, DE, B | Vozilo: NEMA | Licenca ističe: 2028-09-10");
        System.out.println("5. Nikola Đorđević | Email: nikola.djordjevic@autoskola.rs | Kapacitet: 7/5 | Kategorije: A, A1, A2 | Vozilo: NEMA | Licenca ističe: 2026-11-05");

        System.out.println("\n--- VOZILA (sva AVAILABLE, bez instruktora) ---");
        System.out.println("1. NS-123-AB | Volkswagen | 45.230 km | Registracija ističe: 2027-05-15 | Instruktor: NEMA");
        System.out.println("2. BG-456-BC | Renault | 38.450 km | Registracija ističe: 2026-08-20 | Instruktor: NEMA");
        System.out.println("3. NI-789-CD | Fiat | 67.210 km | Registracija ističe: 2025-12-01 | Instruktor: NEMA");
        System.out.println("4. KG-321-DE | Opel | 52.380 km | Registracija ističe: 2028-03-25 | Instruktor: NEMA");
        System.out.println("5. SU-654-EF | Ford | 29.870 km | Registracija ističe: 2026-10-12 | Instruktor: NEMA");

        System.out.println("\n--- VOZILA (sva OUT_OF_SERVICE, bez instruktora) ---");
        System.out.println("6. IN-777-ET | Ford | 20.870 km | Registracija ističe: 2027-10-12 | Instruktor: NEMA");
        System.out.println("7. IN-850-TA | BMW | 30.780 km | Registracija ističe: 2026-10-10 | Instruktor: NEMA");

        System.out.println("\n===================================\n");
    }

    /*private void loadInstructors() {
        String[] names = {"Marko", "Ana", "Stefan", "Jovana", "Nikola", "Maja", "Milan", "Jelena",
                "Petar", "Ivana", "Vladimir", "Tamara", "Uroš", "Milica", "Nenad", "Kristina",
                "Filip", "Sandra", "Dejan", "Nina", "Luka", "Tijana", "Nemanja", "Bojana",
                "Aleksandar", "Vesna", "Dragan", "Snežana", "Igor", "Dragana"};
        String[] lastNames = {"Petrović", "Jovanović", "Nikolić", "Marković", "Đorđević", "Stojanović",
                "Ilić", "Pavlović", "Milošević", "Todorović", "Kovačević", "Popović", "Savić",
                "Kostić", "Lukić", "Radovanović", "Vuković", "Đukić", "Mladenović", "Simić"};

        String[] documentTypes = {
                "INSTRUKTORSKA LICENCA B KATEGORIJE",
                "DIPLOMA ZA INSTRUKTORA, SERTIFIKAT ZA PRVU POMOĆ",
                "SERTIFIKAT ZA KAMIONE, DIPLOMA ZA INSTRUKTORA",
                "INSTRUKTORSKA LICENCA, SERTIFIKAT ZA VOŽNJU U NOĆNIM USLOVIMA",
                "DIPLOMA ZA INSTRUKTORA, SERTIFIKAT ZA PRVU POMOĆ, VOZAČKA DOZVOLA D KATEGORIJE",
                "INSTRUKTORSKA LICENCA, SERTIFIKAT ZA RAD SA POČETNICIMA",
                "SERTIFIKAT ZA TERETNA VOZILA, DIPLOMA ZA INSTRUKTORA",
                "INSTRUKTORSKA LICENCA ZA MOTOCIKLE, SERTIFIKAT ZA PRVU POMOĆ",
                "DIPLOMA ZA INSTRUKTORA ZA AUTOBUSE, SERTIFIKAT ZA PRVU POMOĆ",
                "INSTRUKTORSKA LICENCA, SERTIFIKAT ZA RAD SA OSOBAMA SA POSEBNIM POTREBAMA"
        };

        Category[][] categoryGroups = {
                {Category.B},
                {Category.B, Category.BE},
                {Category.B, Category.A},
                {Category.C, Category.CE},
                {Category.C, Category.CE, Category.BE},
                {Category.D, Category.DE, Category.B},
                {Category.A, Category.A2, Category.B},
                {Category.D, Category.DE, Category.C, Category.CE},
                {Category.B, Category.BE, Category.A},
                {Category.A, Category.A1, Category.A2}
        };

        for (int i = 0; i < 500; i++) {
            InstructorDocument instructor = new InstructorDocument();
            instructor.setId(UUID.randomUUID().toString());

            String name = names[random.nextInt(names.length)];
            String lastName = lastNames[random.nextInt(lastNames.length)];
            instructor.setName(name);
            instructor.setLastName(lastName);
            instructor.setEmail(name.toLowerCase() + "." + lastName.toLowerCase() + i + "@autoskola.rs");

            int maxCapacity = 2 + random.nextInt(14); // 2-15
            instructor.setMaxCapacity(maxCapacity);

            int currentCount;
            if (random.nextInt(10) < 6) {
                currentCount = random.nextInt(maxCapacity);
            } else {
                currentCount = maxCapacity; // puno
            }
            instructor.setCurrentCandidateCount(currentCount);

            instructor.setVehicleRegistrationNumber(generateRegistrationNumber());
            instructor.setDocumentTypes(documentTypes[random.nextInt(documentTypes.length)]);

            LocalDate licenseDate;
            if (random.nextInt(10) < 7) {
                licenseDate = LocalDate.now().plusDays(30 + random.nextInt(1800));
            } else {
                licenseDate = LocalDate.now().minusDays(1 + random.nextInt(365));
            }
            instructor.setLicenseExpiryDate(licenseDate);

            instructor.setCategories(Arrays.asList(categoryGroups[random.nextInt(categoryGroups.length)]));

            instructorService.saveInstructor(instructor);
        }

        System.out.println("500 instruktora uneto.");
    }

    private void loadVehicles() {
        String[] brands = {"Volkswagen", "Renault", "Fiat", "Opel", "Ford",
                "Toyota", "Škoda", "Peugeot", "BMW", "Hyundai"};
        String[] statuses = {"AVAILABLE", "IN_USE", "OUT_OF_SERVICE"};
        int[] statusWeights = {5, 4, 1};

        String[] instructorNames = {"Marko", "Ana", "Stefan", "Jovana", "Nikola",
                "Maja", "Milan", "Jelena", "Petar", "Ivana"};
        String[] instructorLastNames = {"Petrović", "Jovanović", "Nikolić", "Marković", "Đorđević",
                "Stojanović", "Ilić", "Pavlović", "Milošević", "Todorović"};

        for (int i = 0; i < 500; i++) {
            VehicleDocument vehicle = new VehicleDocument();
            vehicle.setId(UUID.randomUUID().toString());

            String city = new String[]{"NS", "BG", "NI", "KG", "SU"}[random.nextInt(5)];
            int num = 100 + random.nextInt(900);
            String letters = "" + (char)('A' + random.nextInt(26)) + (char)('A' + random.nextInt(26));
            vehicle.setRegistrationNumber(city + "-" + num + "-" + letters + "-" + i);

            vehicle.setBrand(brands[random.nextInt(brands.length)]);

            int statusRoll = random.nextInt(10);
            String status;
            if (statusRoll < 5) status = "AVAILABLE";
            else if (statusRoll < 9) status = "IN_USE";
            else status = "OUT_OF_SERVICE";
            vehicle.setStatus(status);

            vehicle.setCurrentMileage(5000 + random.nextInt(245000));

            LocalDate expiryDate;
            int scenario = random.nextInt(10);
            if (scenario < 2) {
                expiryDate = LocalDate.now().plusDays(1 + random.nextInt(30));
            } else if (scenario < 5) {
                expiryDate = LocalDate.now().plusDays(30 + random.nextInt(150));
            } else {
                expiryDate = LocalDate.now().plusDays(180 + random.nextInt(900));
            }
            vehicle.setRegistrationExpiryDate(expiryDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

            int idx = random.nextInt(instructorNames.length);
            vehicle.setInstructorName(instructorNames[idx]);
            vehicle.setInstructorLastname(instructorLastNames[idx]);

            vehicleService.saveVehicle(vehicle);
        }

        System.out.println("500 vozila uneto.");
    }

    private String generateRegistrationNumber() {
        String[] cities = {"NS", "BG", "NI", "KG", "SU"};
        String city = cities[random.nextInt(cities.length)];
        int number = 100 + random.nextInt(900);
        String letters = "" + (char)('A' + random.nextInt(26)) + (char)('A' + random.nextInt(26));
        return city + "-" + number + "-" + letters;
    } */
}
