package com.example.instructor_management.service;
import com.example.instructor_management.DTO.VehicleAnalyticsDTO;
import com.example.instructor_management.DTO.VehiclePageResponse;
import com.example.instructor_management.model.Instructor;
import com.example.instructor_management.model.Vehicle;
import com.example.instructor_management.repository.InstructorRepository;
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
import java.util.List;
import java.util.Map;

@Service
public class ReportService {
    @Autowired
    private InstructorRepository instructorRepository;

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
                VehicleAnalyticsDTO matched = vehicles.stream()
                        .filter(vd -> vd.getRegistrationNumber().equals(v.getRegistrationNumber()))
                        .findFirst()
                        .orElse(null);

                combinedTable.addCell(new PdfPCell(new Phrase(i.getName() + " " + i.getLastname(), normalFont)));
                combinedTable.addCell(new PdfPCell(new Phrase(v.getRegistrationNumber(), normalFont)));
                combinedTable.addCell(new PdfPCell(new Phrase(matched != null ? matched.getStatus() : "N/A", normalFont)));
                combinedTable.addCell(new PdfPCell(new Phrase(matched != null ? String.valueOf(matched.getCurrentMileage()) : "N/A", normalFont)));
            }
        }
        document.add(combinedTable);
        document.add(new Paragraph(" "));

        document.add(new Paragraph("4. Grafikon - broj vozila po statusu", headerFont));
        document.add(new Paragraph(" "));
        Image chartImage = generateVehicleStatusChart(vehicles);
        document.add(chartImage);

        document.close();
        return out.toByteArray();
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
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Long> countByStatus = vehicles.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        VehicleAnalyticsDTO::getStatus, java.util.stream.Collectors.counting()));

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
