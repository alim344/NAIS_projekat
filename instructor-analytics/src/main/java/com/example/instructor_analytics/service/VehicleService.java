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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final ElasticsearchOperations elasticsearchOperations;

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

    public Map<String, Object> searchVehiclesByBrandAndStatus(String brand, String status) {
        Criteria criteria = new Criteria("brand").matches(brand);

        if (status != null && !status.isEmpty()) {
            criteria = criteria.and(new Criteria("status").is(status));
        }

        CriteriaQuery query = new CriteriaQuery(criteria);
        query.addSort(Sort.by(Sort.Direction.DESC, "currentMileage"));

        SearchHits<VehicleDocument> hits = elasticsearchOperations.search(query, VehicleDocument.class);

        List<VehicleDocument> vehicles = hits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        double avgMileage = vehicles.stream()
                .mapToInt(VehicleDocument::getCurrentMileage)
                .average()
                .orElse(0);

        Map<String, Double> avgMileageByBrand = vehicles.stream()
                .collect(Collectors.groupingBy(
                        VehicleDocument::getBrand,
                        Collectors.averagingInt(VehicleDocument::getCurrentMileage)
                ));

        Map<String, Object> result = new HashMap<>();
        result.put("searchTerm", brand);
        result.put("statusFilter", status);
        result.put("totalFound", vehicles.size());
        result.put("averageMileage", Math.round(avgMileage));
        result.put("avgMileageByBrand", avgMileageByBrand);
        result.put("vehicles", vehicles);

        return result;
    }
}
