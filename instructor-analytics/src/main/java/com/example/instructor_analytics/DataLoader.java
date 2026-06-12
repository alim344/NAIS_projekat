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

      //  instructorService.deleteAll();
      //  vehicleService.deleteAll();

        System.out.println("Stari podaci obrisani, unosimo nove...");

        loadInstructors();
        loadVehicles();

        System.out.println("Test podaci uspesno uneti!");
    }

    private void loadInstructors() {
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
    }
}
