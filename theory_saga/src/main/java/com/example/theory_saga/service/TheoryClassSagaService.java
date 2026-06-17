package com.example.theory_saga.service;

import com.example.theory_saga.dtos.CreateTheoryClassDTO;
import com.example.theory_saga.dtos.SagaResultDTO;
import com.example.theory_saga.dtos.TheoryClassLogDTO;
import com.example.theory_saga.dtos.TheoryClassResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TheoryClassSagaService {

    private final RestTemplate restTemplate;

    @Value("${services.theory-organization.url}")
    private String theoryOrgUrl;

    @Value("${services.theory-search.url}")
    private String theorySearchUrl;

    public SagaResultDTO scheduleTheoryClass(CreateTheoryClassDTO dto){

        Long neo4jClassId = null;
        String elasticSearchId = null;

        try { // pokusam da napravim u neo4j cas
            neo4jClassId = createTheoryClassInNeo4j(dto);
            System.out.println("Kreiran cas u neo4j, ID: " +neo4jClassId);
        } catch (Exception e){
            System.out.println("Neuspesno " + e.getMessage());
            return new SagaResultDTO(false, "Neuspesno kreiranje TheoryClass u Neo4j", null, null);
        }

        try {
            elasticSearchId = createTheoryClassLogInElasticsearch(dto, neo4jClassId);
            System.out.println("TheoryClassLog kreiran u elasticsearch sa ID: " + elasticSearchId);
        } catch (Exception e) {
            System.out.println("NEUSPESAN UPIS elasticsearch: " + e.getMessage());
            System.out.println("Brisanje TheoryClass iz elasticsearch sa ID: " + neo4jClassId);

            try {
                deleteTheoryClassFromNeo4j(neo4jClassId);
                System.out.println("TheoryClass obrisan iz Neo4j");
            } catch (Exception compEx) {
                System.out.println("NEUSPESNO BRISANJE : " + compEx.getMessage());
                return new SagaResultDTO(false,
                        "Kriticna greska - podaci mogu biti nekonzistentni. Neo4j ID: " + neo4jClassId,
                        neo4jClassId, null);
            }

            return new SagaResultDTO(false,
                    "Neuspelo kreiranje loga u Elasticsearch, Neo4j rollback uspeo: " + e.getMessage(),
                    null, null);
        }

        try { // veze u neo4j
            setupTheoryClassRelations(neo4jClassId, dto);
            System.out.println("Relacije postavljene u neo4j");
        } catch(Exception e){ //ako ne uspe, sve se brise
            System.out.println("Relacije NISU postavljene u neo4j" + e.getMessage());
            try {
                deleteTheoryClassFromNeo4j(neo4jClassId);
                deleteTheoryClassLogFromElasticsearch(elasticSearchId);
                System.out.println("SAGA ispravka USPELA: Obrisano iz oba sistema");
            } catch (Exception compEx) {
                System.out.println("SAGA ispravka NEUSPELA: " + compEx.getMessage());
            }

            return new SagaResultDTO(false,
                    "Neuspelo postavljanje relacija, rollback izvršen: " + e.getMessage(),
                    null, null);
        }

        return new SagaResultDTO(true,
                "Teorijski cas uspesno zakazan u oba sistema",
                neo4jClassId,
                elasticSearchId);
    }

    public SagaResultDTO cancelTheoryClass(Long neo4jClassId, String elasticsearchLogId) {
        boolean neo4jDeleted = false;
        boolean esDeleted = false;

        try { //brisanje neo4j
            deleteTheoryClassFromNeo4j(neo4jClassId);
            neo4jDeleted = true;
            System.out.println("TheoryClass obrisan iz Neo4j");
        } catch (Exception e) {
            System.out.println("Otkazivanje neo4j NEUSPEO: " + e.getMessage());
            return new SagaResultDTO(false, "Neuspelo brisanje iz Neo4j: " + e.getMessage(), neo4jClassId, elasticsearchLogId);
        }

        try { //brisanje elasticsearch
            deleteTheoryClassLogFromElasticsearch(elasticsearchLogId);
            esDeleted = true;
            System.out.println("TheoryClassLog obrisan iz Elasticsearch");
        } catch (Exception e) {
            System.out.println("Otkazivanje elasticsearch NEUSPEO: " + e.getMessage());
            return new SagaResultDTO(false,
                    "Neo4j obrisan ali Elasticsearch nije: " + e.getMessage(),
                    null, elasticsearchLogId);
        }

        return new SagaResultDTO(true, "Teorijski cas uspesno otkazan iz oba sistema", null, null);
    }

    private Long createTheoryClassInNeo4j(CreateTheoryClassDTO dto) {
        String url = theoryOrgUrl + "/api/theory-classes";

        Map<String, Object> body = new HashMap<>();
        body.put("startTime", dto.getStartTime().toString());
        body.put("endTime", dto.getEndTime().toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<TheoryClassResponseDTO> response = restTemplate.postForEntity(
                url, request, TheoryClassResponseDTO.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody().getId();
        }
        throw new RuntimeException("Neo4j vrati neuspesni status: " + response.getStatusCode());
    }

    private void setupTheoryClassRelations(Long classId, CreateTheoryClassDTO dto) {
        String url = theoryOrgUrl + "/api/theory-classes/" + classId
                + "/setup?hallId=" + dto.getHallId() + "&lessonId=" + dto.getLessonId();

        restTemplate.postForEntity(url, null, String.class);
    }

    private String createTheoryClassLogInElasticsearch(CreateTheoryClassDTO dto, Long neo4jClassId) {
        String url = theorySearchUrl + "/api/class-logs";

        int duration = (int) ChronoUnit.MINUTES.between(dto.getStartTime(), dto.getEndTime());

        TheoryClassLogDTO logDto = new TheoryClassLogDTO();
        logDto.setProfessorUsername(dto.getProfessorUsername());
        logDto.setProfessorFullName(dto.getProfessorFullName());
        logDto.setClassroomName(dto.getClassroomName());
        logDto.setClassroomCapacity(dto.getClassroomCapacity());
        logDto.setLessonTitle(dto.getLessonTitle());
        logDto.setLessonOrderNumber(dto.getLessonOrderNumber());
        logDto.setCategory(dto.getCategory());
        logDto.setStartTime(dto.getStartTime());
        logDto.setEndTime(dto.getEndTime());
        logDto.setDurationMinutes(duration);
        logDto.setCandidateCount(0);
        logDto.setAverageCandidateScore(0);
        logDto.setFullyAttended(false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TheoryClassLogDTO> request = new HttpEntity<>(logDto, headers);

        ResponseEntity<TheoryClassLogDTO> response = restTemplate.postForEntity(
                url, request, TheoryClassLogDTO.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody().getId();
        }
        throw new RuntimeException("Elasticsearch vrati neuspesni status: " + response.getStatusCode());
    }

    private void deleteTheoryClassFromNeo4j(Long classId) {
        String url = theoryOrgUrl + "/api/theory-classes/" + classId;
        restTemplate.delete(url);
    }

    private void deleteTheoryClassLogFromElasticsearch(String logId) {
        String url = theorySearchUrl + "/api/class-logs/" + logId;
        restTemplate.delete(url);
    }
}
