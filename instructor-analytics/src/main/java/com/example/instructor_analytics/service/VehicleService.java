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

    public Map<String, Object> getVehiclesByExpiringRegistration(int daysAhead, String status) {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(daysAhead);

        Criteria criteria = new Criteria("registrationExpiryDate")
                .greaterThanEqual(today.toString())
                .lessThanEqual(futureDate.toString());

        if (status != null && !status.isEmpty()) {
            criteria = criteria.and(new Criteria("status").is(status));
        }

        CriteriaQuery query = new CriteriaQuery(criteria);
        query.addSort(Sort.by(Sort.Direction.ASC, "registrationExpiryDate"));

        SearchHits<VehicleDocument> hits = elasticsearchOperations.search(query, VehicleDocument.class);

        List<VehicleDocument> vehicles = hits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        Map<String, Long> countByStatus = vehicles.stream()
                .collect(Collectors.groupingBy(VehicleDocument::getStatus, Collectors.counting()));

        Map<String, Object> result = new HashMap<>();
        result.put("daysAhead", daysAhead);
        result.put("statusFilter", status);
        result.put("totalVehicles", vehicles.size());
        result.put("countByStatus", countByStatus);
        result.put("vehicles", vehicles);

        return result;
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

}
