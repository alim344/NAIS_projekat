package com.example.practical_class_es.service;

import com.example.practical_class_es.doc.PracticalClassLog;
import com.example.practical_class_es.repo.PracticalClassLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PracticalClassLogService {

    @Autowired
    private PracticalClassLogRepository practicalClassLogRepo;

    public PracticalClassLog create(PracticalClassLog log) {
        return practicalClassLogRepo.save(log);
    }

    public Optional<PracticalClassLog> findById(String id) {
        return practicalClassLogRepo.findById(id);
    }

    public List<PracticalClassLog> findAll() {
        return StreamSupport
                .stream(practicalClassLogRepo.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public PracticalClassLog update(String id, PracticalClassLog updated) {
        updated.setId(id);
        return practicalClassLogRepo.save(updated);
    }

    public void delete(String id) {
        practicalClassLogRepo.deleteById(id);
    }



    /* ------CREATE----

        PUT practical-class-log/_doc/1
        {
          "practicalClassId": 101,
          "startTime": "2026-05-21 08:00:00",
          "endTime": "2026-05-21 09:30:00",
          "completed": true,
          "kmDriven": 45,
          "score": 95,
          "consumedFuelLiters": 3.4,
          "instructorInfo": {
            "instructorId": 12,
            "firstName": "Jova",
            "lastName": "Simic"
          },
          "candidateInfo": {
            "candidateId": 1,
            "firstName": "Mila",
            "lastName": "Budim"
          },
          "vehicleInfo": {
            "vehicleId": 5,
            "model": "Volkswagen Golf 8",
            "licensePlate": "NS357WE"
          },
          "instructorNote": "Excellent clutch control, slight hesitation at the roundabouts.",
          "route": "Bulevar Oslobodjenja, Liman 4, Centar"
        }

        # ----FIND BY ID-------

        GET practical-class-log/_doc/1

        # ------READ ALL
        GET practical-class-log/_search
        {
          "query": {
            "match_all": {}
          }
        }

        # -----UPDATE

        POST practical-class-log/_update/1
        {
          "doc": {
            "completed": true,
            "kmDriven": 50,
            "score": 98,
            "instructorNote": "Corrected minor issues with roundabout positioning. Great drive."
          }
        }

        #------ DELETE
        DELETE practical-class-log/_doc/1

    */




}
