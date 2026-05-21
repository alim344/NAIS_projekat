package com.example.class_organization.controller;

import com.example.class_organization.dto.AttendanceDTO;
import com.example.class_organization.dto.AttendanceGetDTO;
import com.example.class_organization.dto.RegistrationDTO;
import com.example.class_organization.model.Candidate;
import com.example.class_organization.service.CandidateService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/candidates")
public class CandidateController {


    @Autowired
    private CandidateService candidateService;

    @PostMapping("/add")
    public ResponseEntity<Candidate> registerCandidate(@RequestBody RegistrationDTO registrationDTO) {
        candidateService.registerCandidate(registrationDTO);
        return ResponseEntity.ok().body(new Candidate());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCandidate(@PathVariable String id) {
        if(candidateService.deleteCandidate(id) >0) {
            return ResponseEntity.ok().body("Candidate deleted");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Candidate>> getCandidates() {
        return ResponseEntity.ok(candidateService.getAllCandidates());
    }

    @PatchMapping("/updateStatus/{id}")
    public ResponseEntity<Candidate> updateCandidateStatus(@PathVariable String id, @RequestParam("newStatus") String newStatus) {
        Candidate candidate = candidateService.updateCandidateStatus(id, newStatus);
        return ResponseEntity.ok(candidate);
    }

    @PatchMapping("/updatePrefLocation/{id}")
    public ResponseEntity<Candidate> updateCandidatePrefLocation(@PathVariable String id, @RequestParam("prefLocation") String prefLocation) {
        Candidate candidate = candidateService.updatePrefLocation(id, prefLocation);
        return ResponseEntity.ok(candidate);
    }


    /*@PatchMapping("/attendance/update")
    public ResponseEntity<String> updateAttendance(String username, Long classId, int km , String note) {
        candidateService.updateAttendance(username, classId, km, note);
        return ResponseEntity.ok().body("Successfully updated attendance");
    }*/

    @PatchMapping("/attendance/update")
    public ResponseEntity<String> updateAttendance(@RequestBody AttendanceDTO dto) {
        try{

            int i =  candidateService.updateAttendance(dto.getUsername(), dto.getClassId(), dto.getKm(), dto.isPresent());

            if(i == 0){
                return ResponseEntity.badRequest().build();
            }

        }catch(DataAccessException e){
            return ResponseEntity.status(500).body("DATAACESSEXCEPTION: " + e.getMessage());
        }
        return ResponseEntity.ok("Attendance updated");
    }

    @GetMapping("/getAttendance/{username}")
    public ResponseEntity<List<AttendanceGetDTO>> getAttendance(@PathVariable String username){
        return ResponseEntity.ok(candidateService.getCandidateAttendance(username));
    }



    @GetMapping("/complex/getCandidatesByInstId/{id}")
    public ResponseEntity<List<Candidate>> getCandidatesByInstructor(@PathVariable String id){
        return ResponseEntity.ok(candidateService.getCandidatesByInstructorId(id));
    }


}
