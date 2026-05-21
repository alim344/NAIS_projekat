package com.example.instructor_analytics.service;

import com.example.instructor_analytics.model.VehicleDocument;
import com.example.instructor_analytics.repository.VehicleRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public VehicleDocument saveVehicle(VehicleDocument vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public Optional<VehicleDocument> getVehicleById(String id) {
        return vehicleRepository.findById(id);
    }

    public Iterable<VehicleDocument> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public VehicleDocument updateVehicle(String id, VehicleDocument updated) {
        updated.setId(id);
        return vehicleRepository.save(updated);
    }

    public void deleteVehicle(String id) {
        vehicleRepository.deleteById(id);
    }


    /**
     * COMPLEX QUERY 2:
     * Vozila kojima registracija istice u narednih X dana
     * + filtriranje po statusu
     * + sortiranje po datumu isteka (rastuce)
     * + agregacija: broj vozila po statusu
     */
    public Map<String, Object> findVehiclesWithExpiringRegistration(
            int daysAhead,
            String status) {

        String today = LocalDate.now().format(DATE_FORMATTER);
        String futureDate = LocalDate.now().plusDays(daysAhead).format(DATE_FORMATTER);

        Criteria criteria = new Criteria("registrationExpiryDate")
                .greaterThanEqual(today)
                .lessThanEqual(futureDate);

        if (status != null && !status.isEmpty()) {
            criteria = criteria.and(new Criteria("status")).is(status);
        }

        CriteriaQuery query = new CriteriaQuery(criteria);
        query.setMaxResults(1000);

        SearchHits<VehicleDocument> searchHits = elasticsearchOperations.search(
                query, VehicleDocument.class
        );

        List<VehicleDocument> vehicles = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        vehicles.sort(Comparator.comparing(VehicleDocument::getRegistrationExpiryDate));

        Map<String, Long> countByStatus = vehicles.stream()
                .collect(Collectors.groupingBy(
                        VehicleDocument::getStatus,
                        Collectors.counting()
                ));

        List<Map<String, Object>> vehicleList = vehicles.stream()
                .map(v -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", v.getId());
                    map.put("registrationNumber", v.getRegistrationNumber());
                    map.put("brand", v.getBrand());
                    map.put("status", v.getStatus());
                    map.put("currentMileage", v.getCurrentMileage());
                    map.put("registrationExpiryDate", v.getRegistrationExpiryDate());
                    map.put("instructorName", v.getInstructorName());
                    map.put("instructorLastname", v.getInstructorLastname());
                    return map;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("daysAhead", daysAhead);
        response.put("statusFilter", status != null ? status : "all");
        response.put("totalVehiclesFound", vehicles.size());
        response.put("countByStatus", countByStatus);
        response.put("vehicles", vehicleList);

        return response;
    }

    /**
     * COMPLEX QUERY 3:
     * Statistika vozila za određeni brend:
     * - Prikaz samo za brend koji korisnik unese
     * - Opcioni filteri: status, minimalna kilometraža
     * - Prosječna kilometraža, broj vozila, ukupna kilometraža
     */
    public Map<String, Object> getVehicleStatisticsByBrand(
            String brand,
            String status,
            Integer minMileage) {

        Criteria criteria = new Criteria("brand").is(brand);

        if (status != null && !status.isEmpty()) {
            criteria = criteria.and(new Criteria("status").is(status));
        }

        if (minMileage != null) {
            criteria = criteria.and(new Criteria("currentMileage").greaterThanEqual(minMileage));
        }

        CriteriaQuery query = new CriteriaQuery(criteria);
        query.setMaxResults(1000);

        SearchHits<VehicleDocument> searchHits = elasticsearchOperations.search(
                query, VehicleDocument.class
        );

        List<VehicleDocument> vehicles = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        double avgMileage = vehicles.stream()
                .mapToInt(VehicleDocument::getCurrentMileage)
                .average()
                .orElse(0.0);

        int totalMileage = vehicles.stream()
                .mapToInt(VehicleDocument::getCurrentMileage)
                .sum();

        List<Map<String, Object>> vehicleList = vehicles.stream()
                .map(v -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", v.getId());
                    map.put("registrationNumber", v.getRegistrationNumber());
                    map.put("brand", v.getBrand());
                    map.put("status", v.getStatus());
                    map.put("currentMileage", v.getCurrentMileage());
                    map.put("registrationExpiryDate", v.getRegistrationExpiryDate());
                    map.put("instructorName", v.getInstructorName());
                    map.put("instructorLastname", v.getInstructorLastname());
                    return map;
                })
                .sorted(Comparator.comparingInt(v -> (Integer) v.get("currentMileage")))
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("brand", brand);
        response.put("statusFilter", status != null ? status : "all");
        response.put("minMileageFilter", minMileage != null ? minMileage : "none");
        response.put("totalVehiclesFound", vehicles.size());
        response.put("averageMileage", Math.round(avgMileage));
        response.put("totalMileage", totalMileage);
        response.put("vehicles", vehicleList);

        return response;
    }

}
