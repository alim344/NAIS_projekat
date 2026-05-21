package com.example.practical_class_es.service;

import com.example.practical_class_es.doc.CandidateAnalytics;
import com.example.practical_class_es.repo.CandidateAnalyticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class CandidateAnalyticsService {

    @Autowired
    private CandidateAnalyticsRepository candidateAnalyticsRepo;


    //CRUD

    public CandidateAnalytics create(CandidateAnalytics candidate) {

        return candidateAnalyticsRepo.save(candidate);
    }


    public Optional<CandidateAnalytics> findById(String id) {

        return candidateAnalyticsRepo.findById(id);
    }

    public List<CandidateAnalytics> findAll() {
        return StreamSupport
                .stream(candidateAnalyticsRepo.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public CandidateAnalytics update(String id, CandidateAnalytics updated) {
        updated.setId(id);
        return candidateAnalyticsRepo.save(updated);
    }

    public void delete(String id) {

        candidateAnalyticsRepo.deleteById(id);
    }

    /* -------CREATE

        PUT candidate-analytics-timeprefs/_doc/1
        {
          "candidateId": 1,
          "name": "Mila",
          "lastname": "Budim",
          "email": "mila@gmail.com",
          "startOfTraining": "2025-01-15",
          "preferredLocation": "Novi Sad",
          "theoryCompleted": true,
          "status": "PRACTICAL",
          "category": "B",
          "totalKmDriven": 80,
          "numberOfHeldClasses": 6,
          "avgClassGrade": 4.3,
          "activePrefs": [
            {
              "date": "2025-06-10",
              "startTime": "08:00",
              "endTime": "11:00",
              "flexibility": "HIGH"
            }
          ]
        }

        # ----FIND BY ID-------

        GET candidate-analytics-timeprefs/_doc/1

        # ------READ ALL
        GET candidate-analytics-timeprefs/_search
        {
          "query": {
            "match_all": {}
          }
        }

        # -----UPDATE

        POST candidate-analytics-timeprefs/_update/1
        {
          "doc": {
            "status": "PRACTICAL",
            "totalKmDriven": 155,
            "avgClassGrade": 4.5
          }
        }

        #------ DELETE
        DELETE candidate-analytics-timeprefs/_doc/1

*/
}
