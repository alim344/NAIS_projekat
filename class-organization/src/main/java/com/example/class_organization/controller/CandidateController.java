package com.example.class_organization.controller;

import com.example.class_organization.dto.RegistrationDTO;
import com.example.class_organization.model.Candidate;
import com.example.class_organization.service.CandidateService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/candidates")
public class CandidateController {


    @Autowired
    private CandidateService candidateService;

    @PostMapping("/add")
    public ResponseEntity<Candidate> registerCandidate(RegistrationDTO registrationDTO) {
        candidateService.registerCandidate(registrationDTO);
        return ResponseEntity.ok().body(new Candidate());
    }

    @PatchMapping("/attendance/update")
    public ResponseEntity<String> updateAttendance(String username, Long classId, int km , String note) {
        candidateService.updateAttendance(username, classId, km, note);
        return ResponseEntity.ok().body("Successfully updated attendance");
    }




}
