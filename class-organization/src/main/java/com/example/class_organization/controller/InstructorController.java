package com.example.class_organization.controller;

import com.example.class_organization.dto.AddingCandidatesDTO;
import com.example.class_organization.dto.InstructorDTO;
import com.example.class_organization.dto.TeachingDTO;
import com.example.class_organization.model.Candidate;
import com.example.class_organization.model.Instructor;
import com.example.class_organization.service.InstructorService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/instructor")
public class InstructorController {

    @Autowired
    private InstructorService instructorService;

    @PatchMapping("/updateTeaching")
    public void updateTeaching(@RequestBody TeachingDTO dto) {
        instructorService.updateTeaching(dto.getUsername(),dto.getClassId(),dto.getScore(), dto.getNote());
    }


    @PostMapping("/register")
    public ResponseEntity<Instructor> registerInstructor(@RequestBody InstructorDTO dto) {
        return ResponseEntity.ok(instructorService.registerInstructor(dto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteInstructor(@PathVariable String id) {

        if(instructorService.deleteInstructor(id) >0) {
            return ResponseEntity.ok("Deleted Instructor");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Instructor>> getAllInstructors() {
        return ResponseEntity.ok(instructorService.getAll());
    }




    @PatchMapping("/addCandidate")
    public ResponseEntity<String> addCandidates(@RequestBody AddingCandidatesDTO dto) {

        int w = instructorService.addCandidate(dto);
        if(w == 1){
            return ResponseEntity.ok("Success");
        }
        return ResponseEntity.badRequest().build();

    }


    @PatchMapping("/deleteCandidate")
    public ResponseEntity<String> deleteCandidate(@RequestBody AddingCandidatesDTO dto){
        int w = instructorService.deleteCandidate(dto);
        if(w == 1){
            return ResponseEntity.ok("Success");
        }
        return ResponseEntity.badRequest().build();
    }


}
