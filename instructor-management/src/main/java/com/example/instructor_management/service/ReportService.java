package com.example.instructor_management.service;
import com.example.instructor_management.DTO.VehicleAnalyticsDTO;
import com.example.instructor_management.DTO.VehiclePageResponse;
import com.example.instructor_management.model.Instructor;
import com.example.instructor_management.model.InstructorDocuments;
import com.example.instructor_management.model.TrainingStatus;
import com.example.instructor_management.model.Vehicle;
import com.example.instructor_management.repository.InstructorRepository;
import com.example.instructor_management.repository.VehicleRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {
    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${instructor.analytics.url}")
    private String analyticsServiceUrl;

    public byte[] generateInstructorVehicleReport() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
        Font headerFont = new Font(Font.HELVETICA, 13, Font.BOLD);
        Font normalFont = new Font(Font.HELVETICA, 10, Font.NORMAL);

        Paragraph title = new Paragraph("Izveštaj o instruktorima i vozilima - Auto škola", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        List<Instructor> instructors = instructorRepository.findAll();
        List<VehicleAnalyticsDTO> vehicles = fetchVehiclesFromAnalytics();

        document.add(new Paragraph("1. Instruktori sa kapacitetom većim od 4", headerFont));
        document.add(new Paragraph(" "));
        PdfPTable instructorTable = new PdfPTable(3);
        instructorTable.setWidthPercentage(100);
        addTableHeader(instructorTable, "Ime", "Prezime", "Kapacitet");
        for (Instructor i : instructors) {
            if (i.getMaxCapacity() != null && i.getMaxCapacity() > 4) {
                instructorTable.addCell(new PdfPCell(new Phrase(i.getName(), normalFont)));
                instructorTable.addCell(new PdfPCell(new Phrase(i.getLastname(), normalFont)));
                instructorTable.addCell(new PdfPCell(new Phrase(String.valueOf(i.getMaxCapacity()), normalFont)));
            }
        }
        document.add(instructorTable);
        document.add(new Paragraph(" "));

        document.add(new Paragraph("2. Dostupna vozila (status AVAILABLE)", headerFont));
        document.add(new Paragraph(" "));
        PdfPTable vehicleTable = new PdfPTable(3);
        vehicleTable.setWidthPercentage(100);
        addTableHeader(vehicleTable, "Registracija", "Marka", "Kilometraža");
        for (VehicleAnalyticsDTO v : vehicles) {
            if ("AVAILABLE".equalsIgnoreCase(v.getStatus())) {
                vehicleTable.addCell(new PdfPCell(new Phrase(v.getRegistrationNumber(), normalFont)));
                vehicleTable.addCell(new PdfPCell(new Phrase(v.getBrand(), normalFont)));
                vehicleTable.addCell(new PdfPCell(new Phrase(String.valueOf(v.getCurrentMileage()), normalFont)));
            }
        }
        document.add(vehicleTable);
        document.add(new Paragraph(" "));

        document.add(new Paragraph("3. Instruktori sa dodeljenim vozilima i njihovom kilometražom", headerFont));
        document.add(new Paragraph(" "));
        PdfPTable combinedTable = new PdfPTable(4);
        combinedTable.setWidthPercentage(100);
        addTableHeader(combinedTable, "Instruktor", "Vozilo (reg.)", "Status", "Kilometraža");

        for (Instructor i : instructors) {
            Vehicle v = i.getVehicle();
            if (v != null) {
                // Status i kilometraza direktno iz Neo4j, ne iz ES-a
                combinedTable.addCell(new PdfPCell(new Phrase(i.getName() + " " + i.getLastname(), normalFont)));
                combinedTable.addCell(new PdfPCell(new Phrase(v.getRegistrationNumber(), normalFont)));
                combinedTable.addCell(new PdfPCell(new Phrase(v.getStatus().toString(), normalFont)));
                combinedTable.addCell(new PdfPCell(new Phrase(String.valueOf(v.getCurrentMileage()), normalFont)));
            }
        }
        document.add(combinedTable);
        document.add(new Paragraph(" "));

        document.add(new Paragraph("4. Grafikon - broj vozila po statusu", headerFont));
        document.add(new Paragraph(" "));
        Image chartImage = generateVehicleStatusChart(vehicles);
        document.add(chartImage);

        // =============================================
        // SLOŽENA SEKCIJA 2: Instruktori sa kandidatima po statusu treninga
        // =============================================
        document.add(new Paragraph("5. Instruktori sa brojem kandidata po statusu treninga", headerFont));
        document.add(new Paragraph(" "));

        PdfPTable candidateStatusTable = new PdfPTable(6);
        candidateStatusTable.setWidthPercentage(100);
        addTableHeader(candidateStatusTable,
                "Instruktor", "Ukupno kandidata", "THEORY", "PRACTICAL", "PASSED", "PENDING");

        for (Instructor i : instructors) {
            if (i.getCandidates() == null || i.getCandidates().isEmpty()) continue;

            long theory = i.getCandidates().stream()
                    .filter(c -> c.getTrainingStatus() == TrainingStatus.THEORY).count();
            long practical = i.getCandidates().stream()
                    .filter(c -> c.getTrainingStatus() == TrainingStatus.PRACTICAL).count();
            long passed = i.getCandidates().stream()
                    .filter(c -> c.getTrainingStatus() == TrainingStatus.PASSED).count();
            long pending = i.getCandidates().stream()
                    .filter(c -> c.getTrainingStatus() == TrainingStatus.PENDING).count();

            candidateStatusTable.addCell(new PdfPCell(new Phrase(i.getName() + " " + i.getLastname(), normalFont)));
            candidateStatusTable.addCell(new PdfPCell(new Phrase(String.valueOf(i.getCandidates().size()), normalFont)));
            candidateStatusTable.addCell(new PdfPCell(new Phrase(String.valueOf(theory), normalFont)));
            candidateStatusTable.addCell(new PdfPCell(new Phrase(String.valueOf(practical), normalFont)));
            candidateStatusTable.addCell(new PdfPCell(new Phrase(String.valueOf(passed), normalFont)));
            candidateStatusTable.addCell(new PdfPCell(new Phrase(String.valueOf(pending), normalFont)));
        }
        document.add(candidateStatusTable);
        document.add(new Paragraph(" "));

        // Grafikon 2: Pie chart - raspodela kandidata po TrainingStatus
        document.add(new Paragraph("6. Grafikon - raspodela kandidata po statusu treninga", headerFont));
        document.add(new Paragraph(" "));
        Image pieChart = generateCandidateStatusPieChart(instructors);
        document.add(pieChart);
        document.add(new Paragraph(" "));

        // =============================================
        // SLOŽENA SEKCIJA 3: Instruktori sa dokumentima koji ističu u 30 dana
        // =============================================
        document.add(new Paragraph("7. Instruktori sa dokumentima koji ističu u narednih 30 dana", headerFont));
        document.add(new Paragraph(" "));

        PdfPTable expiryTable = new PdfPTable(4);
        expiryTable.setWidthPercentage(100);
        addTableHeader(expiryTable, "Instruktor", "Tip dokumenta", "Datum isteka", "Dana do isteka");

        LocalDate today = LocalDate.now();
        LocalDate in30Days = today.plusDays(30);

        for (Instructor i : instructors) {
            if (i.getDocuments() == null) continue;
            for (InstructorDocuments doc : i.getDocuments()) {
                if (doc.getExpiryDate() != null
                        && !doc.getExpiryDate().isBefore(today)
                        && !doc.getExpiryDate().isAfter(in30Days)) {

                    long daysLeft = java.time.temporal.ChronoUnit.DAYS.between(today, doc.getExpiryDate());
                    expiryTable.addCell(new PdfPCell(new Phrase(i.getName() + " " + i.getLastname(), normalFont)));
                    expiryTable.addCell(new PdfPCell(new Phrase(doc.getDocumentType(), normalFont)));
                    expiryTable.addCell(new PdfPCell(new Phrase(doc.getExpiryDate().toString(), normalFont)));
                    expiryTable.addCell(new PdfPCell(new Phrase(String.valueOf(daysLeft), normalFont)));
                }
            }
        }
        document.add(expiryTable);
        document.add(new Paragraph(" "));

        // Grafikon 3: Bar chart - broj instruktora po opterecenju kandidatima
        document.add(new Paragraph("8. Grafikon - broj instruktora po opterećenju kandidatima", headerFont));
        document.add(new Paragraph(" "));
        Image loadChart = generateInstructorLoadChart(instructors);
        document.add(loadChart);

        document.close();
        return out.toByteArray();

    }

    // Grafikon 2: Pie chart
    private Image generateCandidateStatusPieChart(List<Instructor> instructors) throws Exception {
        org.jfree.data.general.DefaultPieDataset dataset = new org.jfree.data.general.DefaultPieDataset();

        long theory = instructors.stream().flatMap(i -> i.getCandidates().stream())
                .filter(c -> c.getTrainingStatus() == TrainingStatus.THEORY).count();
        long practical = instructors.stream().flatMap(i -> i.getCandidates().stream())
                .filter(c -> c.getTrainingStatus() == TrainingStatus.PRACTICAL).count();
        long passed = instructors.stream().flatMap(i -> i.getCandidates().stream())
                .filter(c -> c.getTrainingStatus() == TrainingStatus.PASSED).count();
        long pending = instructors.stream().flatMap(i -> i.getCandidates().stream())
                .filter(c -> c.getTrainingStatus() == TrainingStatus.PENDING).count();

        if (theory > 0) dataset.setValue("THEORY", theory);
        if (practical > 0) dataset.setValue("PRACTICAL", practical);
        if (passed > 0) dataset.setValue("PASSED", passed);
        if (pending > 0) dataset.setValue("PENDING", pending);

        JFreeChart chart = ChartFactory.createPieChart(
                "Raspodela kandidata po statusu treninga", dataset, true, true, false);

        // Dodaj procente
        org.jfree.chart.plot.PiePlot plot = (org.jfree.chart.plot.PiePlot) chart.getPlot();
        plot.setLabelGenerator(new org.jfree.chart.labels.StandardPieSectionLabelGenerator(
                "{0}: {2}", new java.text.DecimalFormat("0"), new java.text.DecimalFormat("0.0%")));

        BufferedImage bufferedImage = chart.createBufferedImage(500, 300);
        ByteArrayOutputStream chartOut = new ByteArrayOutputStream();
        javax.imageio.ImageIO.write(bufferedImage, "png", chartOut);
        return Image.getInstance(chartOut.toByteArray());
    }

    // Grafikon 3: Bar chart - opterecenje instruktora
    private Image generateInstructorLoadChart(List<Instructor> instructors) throws Exception {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        long noCandidates = instructors.stream()
                .filter(i -> i.getCandidates() == null || i.getCandidates().isEmpty()).count();
        long fewCandidates = instructors.stream()
                .filter(i -> i.getCandidates() != null
                        && i.getCandidates().size() >= 1
                        && i.getCandidates().size() <= 2).count();
        long manyCandidates = instructors.stream()
                .filter(i -> i.getCandidates() != null && i.getCandidates().size() >= 3).count();

        dataset.addValue(noCandidates, "Instruktori", "0 kandidata");
        dataset.addValue(fewCandidates, "Instruktori", "1-2 kandidata");
        dataset.addValue(manyCandidates, "Instruktori", "3+ kandidata");

        JFreeChart chart = ChartFactory.createBarChart(
                "Opterećenje instruktora po broju kandidata",
                "Opterećenje", "Broj instruktora",
                dataset, PlotOrientation.VERTICAL, false, true, false);

        BufferedImage bufferedImage = chart.createBufferedImage(500, 300);
        ByteArrayOutputStream chartOut = new ByteArrayOutputStream();
        javax.imageio.ImageIO.write(bufferedImage, "png", chartOut);
        return Image.getInstance(chartOut.toByteArray());
    }

    private void addTableHeader(PdfPTable table, String... headers) {
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, new Font(Font.HELVETICA, 11, Font.BOLD)));
            cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
            table.addCell(cell);
        }
    }

    private List<VehicleAnalyticsDTO> fetchVehiclesFromAnalytics() {
        VehiclePageResponse response = restTemplate.getForObject(
                analyticsServiceUrl + "/api/vehicles", VehiclePageResponse.class);
        return response != null && response.getContent() != null ? response.getContent() : List.of();
    }

    private Image generateVehicleStatusChart(List<VehicleAnalyticsDTO> vehicles) throws Exception {
        // Ovo zameni da vuces iz Neo4j
        List<Vehicle> neo4jVehicles = vehicleRepository.findAll(); // dodaj @Autowired VehicleRepository

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Long> countByStatus = neo4jVehicles.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        v -> v.getStatus().toString(), java.util.stream.Collectors.counting()));

        for (Map.Entry<String, Long> entry : countByStatus.entrySet()) {
            dataset.addValue(entry.getValue(), "Broj vozila", entry.getKey());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Broj vozila po statusu", "Status", "Broj vozila",
                dataset, PlotOrientation.VERTICAL, false, true, false);

        BufferedImage bufferedImage = chart.createBufferedImage(500, 300);
        ByteArrayOutputStream chartOut = new ByteArrayOutputStream();
        javax.imageio.ImageIO.write(bufferedImage, "png", chartOut);
        return Image.getInstance(chartOut.toByteArray());
    }
}
