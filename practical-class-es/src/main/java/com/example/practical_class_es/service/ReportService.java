package com.example.practical_class_es.service;

import org.springframework.stereotype.Service;

import com.example.practical_class_es.doc.CandidateAnalytics;
import com.example.practical_class_es.doc.PracticalClassLog;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.beans.factory.annotation.Autowired;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
@Service
public class ReportService {

    @Autowired
    private QueryService queryService;

    @Autowired
    private CandidateAnalyticsService candidateAnalyticsService;

    @Autowired
    private PracticalClassLogService practicalClassLogService;

    private static final Color HEADER_COLOR   = new Color(41, 128, 185);
    private static final Color ROW_ALT_COLOR  = new Color(235, 245, 255);
    private static final Color WHITE          = Color.WHITE;
    private static final Color GRAY           = Color.GRAY;

    private static final Font FONT_TITLE    = new Font(Font.HELVETICA, 22, Font.BOLD,   new Color(41, 128, 185));
    private static final Font FONT_SUBTITLE = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.GRAY);
    private static final Font FONT_SECTION  = new Font(Font.HELVETICA, 13, Font.BOLD,   new Color(41, 128, 185));
    private static final Font FONT_DESC     = new Font(Font.HELVETICA,  9, Font.NORMAL, Color.DARK_GRAY);
    private static final Font FONT_HEADER   = new Font(Font.HELVETICA,  9, Font.BOLD,   Color.WHITE);
    private static final Font FONT_CELL     = new Font(Font.HELVETICA,  8, Font.NORMAL, Color.BLACK);
    private static final Font FONT_BOLD     = new Font(Font.HELVETICA, 10, Font.BOLD,   Color.BLACK);


    public byte[] generateReport() throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 54, 36);
        PdfWriter.getInstance(document, baos);
        document.open();

        // * Naslov **********
        Paragraph title = new Paragraph("Izvestaj - Auto Skola", FONT_TITLE);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(4);
        document.add(title);

        Paragraph subtitle = new Paragraph(
                "Analiza casova i kandidata | " + java.time.LocalDate.now(), FONT_SUBTITLE);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(18);
        document.add(subtitle);

        // * 1 svi praktični časovi ─
        addSectionTitle(document, "1. Pregled prakticnih casova ");

        List<PracticalClassLog> allLogs = practicalClassLogService.findAll();
        document.add(buildPracticalClassTable(allLogs));

        // * kandidati PRACTICAL
        addSectionTitle(document, "2. Kandidati u fazi prakticne obuke ");


        List<CandidateAnalytics> activeCandidates = candidateAnalyticsService.findAll()
                .stream()
                .filter(c -> "PRACTICAL".equals(c.getStatus()) && c.isTheoryCompleted())
                .toList();
        document.add(buildCandidateTable(activeCandidates));

        // analiya po kategoriji
        addSectionTitle(document, "3. Analiza performansi po kategoriji vozacke dozvole (Elasticsearch)");


        Map<String, Object> complexResult = queryService.searchClassesByTextAndPerformance(
                null, null, null, null, true);


        List<Map<String, Object>> statsByCategory =
                (List<Map<String, Object>>) complexResult.get("statsByCategory");

        document.add(buildCategoryStatsTable(statsByCategory));

        Double overallAvg = (Double) complexResult.get("overallAverageScore");
        Paragraph avg = new Paragraph(
                String.format("Ukupna prosecna ocena svih casova: %.2f", overallAvg), FONT_BOLD);
        avg.setSpacingBefore(8);
        document.add(avg);



//GRAFIKON
        addSectionTitle(document, "5. Grafikon: Broj kandidata po  lokaciji");

        List<CandidateAnalytics> allCandidatesForChart = candidateAnalyticsService.findAll();
        byte[] chart2 = generateLocationChart(allCandidatesForChart);
        Image img2 = Image.getInstance(chart2);
        img2.scaleToFit(500, 260);
        img2.setAlignment(Element.ALIGN_CENTER);
        document.add(img2);

        document.close();
        return baos.toByteArray();
    }




    private void addSectionTitle(Document doc, String text) throws Exception {
        Paragraph p = new Paragraph(text, FONT_SECTION);
        p.setSpacingBefore(16);
        p.setSpacingAfter(4);
        doc.add(p);

        PdfPTable line = new PdfPTable(1);
        line.setWidthPercentage(100);
        PdfPCell lineCell = new PdfPCell(new Phrase(""));
        lineCell.setBorderWidthBottom(1.5f);
        lineCell.setBorderColorBottom(HEADER_COLOR);
        lineCell.setBorderWidthTop(0);
        lineCell.setBorderWidthLeft(0);
        lineCell.setBorderWidthRight(0);
        lineCell.setFixedHeight(2);
        line.addCell(lineCell);
        doc.add(line);
    }

    private void addDescription(Document doc, String text) throws Exception {
        Paragraph p = new Paragraph(text, FONT_DESC);
        p.setSpacingBefore(4);
        p.setSpacingAfter(8);
        doc.add(p);
    }


    private PdfPTable buildPracticalClassTable(List<PracticalClassLog> logs) throws Exception {
        float[] widths = {1.2f, 2f, 2f, 0.8f, 0.8f, 2f, 1f};
        PdfPTable table = new PdfPTable(widths);
        table.setWidthPercentage(100);
        table.setSpacingBefore(6);

        String[] headers = {"Class ID", "Pocetak", "Kraj", "Ocena", "Km", "Kandidat", "Zavrsen"};
        for (String h : headers) addHeaderCell(table, h);

        if (logs.isEmpty()) {

            return table;
        }

        boolean alt = false;
        for (PracticalClassLog log : logs) {
            Color bg = alt ? ROW_ALT_COLOR : WHITE;
            String candidate = log.getCandidateInfo() != null
                    ? log.getCandidateInfo().getName() + " " + log.getCandidateInfo().getLastName()
                    : "-";
            addDataCell(table, nvl(log.getPracticalClassId()), bg);
            addDataCell(table, nvl(log.getStartTime()), bg);
            addDataCell(table, nvl(log.getEndTime()), bg);
            addDataCell(table, String.valueOf(log.getScore()), bg);
            addDataCell(table, log.getKmDriven() != null ? log.getKmDriven().toString() : "-", bg);
            addDataCell(table, candidate, bg);
            addDataCell(table, log.isCompleted() ? "Da" : "Ne", bg);
            alt = !alt;
        }
        return table;
    }


    private PdfPTable buildCandidateTable(List<CandidateAnalytics> candidates) throws Exception {
        float[] widths = {2f, 2f, 1.2f, 1.5f, 1.5f, 1.5f};
        PdfPTable table = new PdfPTable(widths);
        table.setWidthPercentage(100);
        table.setSpacingBefore(6);

        String[] headers = {"Ime", "Prezime", "Kategorija", "Avg. Ocena", "Km ukupno", "Casova"};
        for (String h : headers) addHeaderCell(table, h);

        if (candidates.isEmpty()) {

            return table;
        }

        boolean alt = false;
        for (CandidateAnalytics c : candidates) {
            Color bg = alt ? ROW_ALT_COLOR : WHITE;
            addDataCell(table, nvl(c.getName()), bg);
            addDataCell(table, nvl(c.getLastname()), bg);
            addDataCell(table, nvl(c.getCategory()), bg);
            addDataCell(table, c.getAvgClassGrade() != null
                    ? String.format("%.1f", c.getAvgClassGrade()) : "-", bg);
            addDataCell(table, c.getTotalKmDriven() != null
                    ? c.getTotalKmDriven().toString() : "-", bg);
            addDataCell(table, c.getNumberOfHeldClasses() != null
                    ? c.getNumberOfHeldClasses().toString() : "-", bg);
            alt = !alt;
        }
        return table;
    }


    private PdfPTable buildCategoryStatsTable(List<Map<String, Object>> stats) throws Exception {
        float[] widths = {1.2f, 1.5f, 1.5f, 1.5f, 2f};
        PdfPTable table = new PdfPTable(widths);
        table.setWidthPercentage(100);
        table.setSpacingBefore(6);

        String[] headers = {"Kategorija", "Br. casova", "Avg. ocena", "Ukupno km", "Avg. gorivo (L)"};
        for (String h : headers) addHeaderCell(table, h);

        if (stats == null || stats.isEmpty()) {
            PdfPCell empty = new PdfPCell(new Phrase("Nema podataka", FONT_CELL));
            empty.setColspan(5);
            empty.setHorizontalAlignment(Element.ALIGN_CENTER);
            empty.setPadding(8);
            table.addCell(empty);
            return table;
        }

        boolean alt = false;
        for (Map<String, Object> row : stats) {
            Color bg = alt ? ROW_ALT_COLOR : WHITE;
            addDataCell(table, String.valueOf(row.getOrDefault("category", "-")), bg);
            addDataCell(table, String.valueOf(row.getOrDefault("numberOfClasses", "-")), bg);
            Object avgScore = row.get("averageScore");
            addDataCell(table, avgScore != null
                    ? String.format("%.2f", ((Number) avgScore).doubleValue()) : "-", bg);
            addDataCell(table, String.valueOf(row.getOrDefault("totalKmDriven", "-")), bg);
            Object fuel = row.get("averageFuelConsumption");
            addDataCell(table, fuel != null
                    ? String.format("%.2f", ((Number) fuel).doubleValue()) : "-", bg);
            alt = !alt;
        }
        return table;
    }




    private void addHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FONT_HEADER));
        cell.setBackgroundColor(HEADER_COLOR);
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorderColor(Color.WHITE);
        table.addCell(cell);
    }

    private void addDataCell(PdfPTable table, String text, Color bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FONT_CELL));
        cell.setBackgroundColor(bg);
        cell.setPadding(4);
        cell.setBorderColor(new Color(200, 200, 200));
        table.addCell(cell);
    }

    private String nvl(Object o) {
        return o != null ? o.toString() : "-";
    }

// Grafikon
    private byte[] generateLocationChart(List<CandidateAnalytics> candidates) throws Exception {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (candidates != null) {
            Map<String, Long> perLocation = candidates.stream()
                    .filter(c -> c.getPreferredLocation() != null)
                    .collect(java.util.stream.Collectors.groupingBy(
                            CandidateAnalytics::getPreferredLocation,
                            java.util.stream.Collectors.counting()
                    ));

            perLocation.entrySet().stream()
                    .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                    .forEach(e -> dataset.addValue(e.getValue(), "Kandidati", e.getKey()));
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Broj kandidata po  lokaciji",
                "Lokacija", "Broj kandidata",
                dataset, PlotOrientation.VERTICAL,
                false, true, false);

        chart.setBackgroundPaint(Color.WHITE);

        org.jfree.chart.renderer.category.BarRenderer renderer =
                (org.jfree.chart.renderer.category.BarRenderer)
                        ((org.jfree.chart.plot.CategoryPlot) chart.getPlot()).getRenderer();
        renderer.setSeriesPaint(0, new Color(41, 128, 185));

        BufferedImage img = chart.createBufferedImage(680, 320);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(img, "PNG", out);
        return out.toByteArray();
    }
}
