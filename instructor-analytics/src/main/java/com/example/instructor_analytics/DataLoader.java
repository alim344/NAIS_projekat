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
import java.util.stream.Collectors;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private InstructorService instructorService;

    @Autowired
    private VehicleService vehicleService;

    private final Random random = new Random();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final List<InstructorData> INSTRUCTOR_DATA = Arrays.asList(
            new InstructorData("Marko", "Petrović", "marko.petrovic@autoskola.rs", 5, "C", Arrays.asList("B", "BE"), "DIPLOMA ZA INSTRUKTORA, SERTIFIKAT ZA PRVU POMOĆ", "2027-05-15"),
            new InstructorData("Ana", "Jovanović", "ana.jovanovic@autoskola.rs", 3, "B", Arrays.asList("B", "A"), "INSTRUKTORSKA LICENCA B KATEGORIJE", "2026-12-10"),
            new InstructorData("Stefan", "Nikolić", "stefan.nikolic@autoskola.rs", 8, "CE", Arrays.asList("C", "CE", "BE"), "SERTIFIKAT ZA KAMIONE, DIPLOMA ZA INSTRUKTORA", "2025-08-20"),
            new InstructorData("Jovana", "Marković", "jovana.markovic@autoskola.rs", 2, "B", Arrays.asList("B", "BE"), "INSTRUKTORSKA LICENCA, SERTIFIKAT ZA VOŽNJU U NOĆNIM USLOVIMA", "2028-03-01"),
            new InstructorData("Nikola", "Đorđević", "nikola.djordjevic@autoskola.rs", 10, "D", Arrays.asList("D", "DE", "B"), "DIPLOMA ZA INSTRUKTORA, SERTIFIKAT ZA PRVU POMOĆ, VOZAČKA DOZVOLA D KATEGORIJE", "2024-11-30"),
            new InstructorData("Maja", "Stojanović", "maja.stojanovic@autoskola.rs", 4, "B", Arrays.asList("B", "BE", "A"), "INSTRUKTORSKA LICENCA, SERTIFIKAT ZA RAD SA POČETNICIMA", "2027-09-14"),
            new InstructorData("Milan", "Ilić", "milan.ilic@autoskola.rs", 6, "C", Arrays.asList("C", "CE"), "SERTIFIKAT ZA TERETNA VOZILA, DIPLOMA ZA INSTRUKTORA", "2026-06-22"),
            new InstructorData("Jelena", "Pavlović", "jelena.pavlovic@autoskola.rs", 1, "B", Arrays.asList("B"), "INSTRUKTORSKA LICENCA", "2029-01-05"),
            new InstructorData("Petar", "Milošević", "petar.milosevic@autoskola.rs", 12, "BE", Arrays.asList("B", "BE", "C"), "DIPLOMA ZA INSTRUKTORA, SERTIFIKAT ZA VOŽNJU SA PRIKOLICOM", "2025-04-18"),
            new InstructorData("Ivana", "Todorović", "ivana.todorovic@autoskola.rs", 7, "A", Arrays.asList("A", "A2", "B"), "INSTRUKTORSKA LICENCA ZA MOTOCIKLE, SERTIFIKAT ZA PRVU POMOĆ", "2028-07-30"),
            new InstructorData("Vladimir", "Kovačević", "vladimir.kovacevic@autoskola.rs", 9, "D", Arrays.asList("D", "DE", "B"), "DIPLOMA ZA INSTRUKTORA ZA AUTOBUSE, SERTIFIKAT ZA PRVU POMOĆ", "2024-09-12"),
            new InstructorData("Tamara", "Popović", "tamara.popovic@autoskola.rs", 3, "B", Arrays.asList("B", "BE", "A"), "INSTRUKTORSKA LICENCA, SERTIFIKAT ZA RAD SA OSOBAMA SA POSEBNIM POTREBAMA", "2027-11-20"),
            new InstructorData("Uroš", "Savić", "uros.savic@autoskola.rs", 11, "CE", Arrays.asList("C", "CE", "BE"), "DIPLOMA ZA INSTRUKTORA, SERTIFIKAT ZA VOŽNJU U TEŠKIM USLOVIMA", "2025-02-28"),
            new InstructorData("Milica", "Kostić", "milica.kostic@autoskola.rs", 5, "B", Arrays.asList("B"), "INSTRUKTORSKA LICENCA, SERTIFIKAT ZA PRVU POMOĆ", "2028-12-15"),
            new InstructorData("Nenad", "Lukić", "nenad.lukic@autoskola.rs", 15, "DE", Arrays.asList("D", "DE", "B", "BE"), "DIPLOMA ZA INSTRUKTORA, SERTIFIKAT ZA PRVU POMOĆ, VOZAČKA DOZVOLA DE KATEGORIJE", "2024-04-01"),
            new InstructorData("Kristina", "Radovanović", "kristina.radovanovic@autoskola.rs", 2, "A", Arrays.asList("A", "A1", "A2"), "INSTRUKTORSKA LICENCA ZA MOTOCIKLE", "2029-06-18"),
            new InstructorData("Filip", "Vuković", "filip.vukovic@autoskola.rs", 6, "C", Arrays.asList("C", "CE", "B"), "SERTIFIKAT ZA TERETNA VOZILA, DIPLOMA ZA INSTRUKTORA", "2027-10-03"),
            new InstructorData("Sandra", "Đukić", "sandra.djukic@autoskola.rs", 4, "B", Arrays.asList("B", "BE"), "INSTRUKTORSKA LICENCA, SERTIFIKAT ZA VOŽNJU U GRADSKOJ VOŽNJI", "2028-08-25"),
            new InstructorData("Dejan", "Mladenović", "dejan.mladenovic@autoskola.rs", 13, "DE", Arrays.asList("D", "DE", "C", "CE"), "DIPLOMA ZA INSTRUKTORA, SERTIFIKAT ZA PRVU POMOĆ, VOZAČKA DOZVOLA DE KATEGORIJE", "2025-01-19"),
            new InstructorData("Nina", "Simić", "nina.simic@autoskola.rs", 3, "B", Arrays.asList("B", "A"), "INSTRUKTORSKA LICENCA, SERTIFIKAT ZA RAD SA POČETNICIMA", "2029-03-27")
    );

    private static final List<VehicleData> VEHICLE_DATA = Arrays.asList(
            new VehicleData("NS-101-AB", "AVAILABLE", 45000, "2026-05-15", "Volkswagen", "Marko", "Petrović"),
            new VehicleData("NS-102-AB", "IN_USE", 32000, "2025-11-20", "Renault", "Ana", "Jovanović"),
            new VehicleData("NS-103-AB", "AVAILABLE", 89000, "2024-08-10", "Fiat", "Stefan", "Nikolić"),
            new VehicleData("NS-104-AB", "OUT_OF_SERVICE", 120000, "2024-12-01", "Opel", "Jovana", "Marković"),
            new VehicleData("NS-105-AB", "AVAILABLE", 25000, "2027-03-15", "Ford", "Nikola", "Đorđević"),
            new VehicleData("NS-106-AB", "IN_USE", 67000, "2026-09-30", "Toyota", "Maja", "Stojanović"),
            new VehicleData("NS-107-AB", "AVAILABLE", 54000, "2025-07-22", "Škoda", "Milan", "Ilić"),
            new VehicleData("NS-108-AB", "AVAILABLE", 8900, "2029-01-14", "Peugeot", "Jelena", "Pavlović"),
            new VehicleData("NS-109-AB", "IN_USE", 156000, "2024-06-08", "Volkswagen", "Petar", "Milošević"),
            new VehicleData("NS-110-AB", "AVAILABLE", 78000, "2027-10-12", "Renault", "Ivana", "Todorović"),
            new VehicleData("NS-111-AB", "OUT_OF_SERVICE", 210000, "2024-03-20", "Fiat", "Vladimir", "Kovačević"),
            new VehicleData("NS-112-AB", "AVAILABLE", 34000, "2028-05-05", "Ford", "Tamara", "Popović"),
            new VehicleData("NS-113-AB", "IN_USE", 112000, "2025-12-18", "Opel", "Uroš", "Savić"),
            new VehicleData("NS-114-AB", "AVAILABLE", 56000, "2026-08-27", "Toyota", "Milica", "Kostić"),
            new VehicleData("NS-115-AB", "IN_USE", 89000, "2024-10-07", "Škoda", "Nenad", "Lukić"),
            new VehicleData("NS-116-AB", "AVAILABLE", 23000, "2028-11-11", "Peugeot", "Kristina", "Radovanović"),
            new VehicleData("NS-117-AB", "AVAILABLE", 67000, "2027-02-14", "Volkswagen", "Filip", "Vuković"),
            new VehicleData("NS-118-AB", "IN_USE", 45000, "2026-07-09", "Renault", "Sandra", "Đukić"),
            new VehicleData("NS-119-AB", "AVAILABLE", 123000, "2025-04-03", "Fiat", "Dejan", "Mladenović"),
            new VehicleData("NS-120-AB", "OUT_OF_SERVICE", 187000, "2024-09-25", "Opel", "Nina", "Simić")
    );

    @Override
    public void run(String... args) throws Exception {
        System.out.println("DataLoader started...");

        if (instructorService.countInstructors() >= 20) {
            System.out.println("Podaci vec postoje (20+ instruktora), preskacemo ucitavanje.");
            return;
        }

        System.out.println("Unosimo test podatke za auto skolu...");

       // instructorService.deleteAll();
       // vehicleService.deleteAll();

        for (InstructorData data : INSTRUCTOR_DATA) {
            InstructorDocument instructor = new InstructorDocument();
            instructor.setId(UUID.randomUUID().toString());
            instructor.setName(data.name);
            instructor.setLastName(data.lastName);
            instructor.setEmail(data.email);
            instructor.setMaxCapacity(data.maxCapacity);
            instructor.setCurrentCandidateCount(random.nextInt(data.maxCapacity)); // 0 do maxCapacity-1
            instructor.setVehicleRegistrationNumber(generateRegistrationNumber());
            instructor.setDocumentTypes(data.documentTypes);
            instructor.setLicenseExpiryDate(LocalDate.parse(data.licenseExpiryDate));
            List<Category> categoryEnums = data.categories.stream()
                    .map(Category::valueOf)
                    .collect(Collectors.toList());
            instructor.setCategories(categoryEnums);

            instructorService.saveInstructor(instructor);
        }

        for (VehicleData data : VEHICLE_DATA) {
            VehicleDocument vehicle = new VehicleDocument();
            vehicle.setId(UUID.randomUUID().toString());
            vehicle.setRegistrationNumber(data.registrationNumber);
            vehicle.setStatus(data.status);
            vehicle.setCurrentMileage(data.currentMileage);
            vehicle.setRegistrationExpiryDate(data.registrationExpiryDate);
            vehicle.setBrand(data.brand);
            vehicle.setInstructorName(data.instructorName);
            vehicle.setInstructorLastname(data.instructorLastname);

            vehicleService.saveVehicle(vehicle);
        }

        System.out.println("Test podaci uspesno uneti!");
    }

    private String generateRegistrationNumber() {
        String[] cities = {"NS", "BG", "NI", "KG", "SU"};
        String city = cities[random.nextInt(cities.length)];
        int number = 100 + random.nextInt(900);
        String letters = "" + (char) ('A' + random.nextInt(26)) + (char) ('A' + random.nextInt(26));
        return city + "-" + number + "-" + letters;
    }

    private static class InstructorData {
        String name, lastName, email, licenseExpiryDate, documentTypes;
        int maxCapacity;
        List<String> categories;

        InstructorData(String name, String lastName, String email, int maxCapacity, String licenseCategory,
                       List<String> categories, String documentTypes, String licenseExpiryDate) {
            this.name = name;
            this.lastName = lastName;
            this.email = email;
            this.maxCapacity = maxCapacity;
            this.licenseExpiryDate = licenseExpiryDate;
            this.documentTypes = documentTypes;
            this.categories = categories;
        }
    }

    private static class VehicleData {
        String registrationNumber, status, registrationExpiryDate, brand, instructorName, instructorLastname;
        int currentMileage;

        VehicleData(String registrationNumber, String status, int currentMileage,
                    String registrationExpiryDate, String brand, String instructorName, String instructorLastname) {
            this.registrationNumber = registrationNumber;
            this.status = status;
            this.currentMileage = currentMileage;
            this.registrationExpiryDate = registrationExpiryDate;
            this.brand = brand;
            this.instructorName = instructorName;
            this.instructorLastname = instructorLastname;
        }
    }
}