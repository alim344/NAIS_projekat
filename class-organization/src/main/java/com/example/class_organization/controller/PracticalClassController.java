package com.example.class_organization.controller;

import com.example.class_organization.dto.PracticalClassDTO;
import com.example.class_organization.model.PracticalClass;
import com.example.class_organization.service.PracticalClassService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/practicalclass")
public class PracticalClassController {

    @Autowired
    private PracticalClassService practicalClassService;

    @PostMapping("/create")
    public ResponseEntity<String> createClass(@RequestBody PracticalClassDTO practicalClassDTO) {
        practicalClassService.createClass(practicalClassDTO);
        return ResponseEntity.ok("PracticalClass created");
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<PracticalClass>> getAll(){
        List<PracticalClass> dtos = practicalClassService.getAllPracticalClasses();
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteClass(@PathVariable String id) {
        boolean deleted = practicalClassService.deleteClass(id);

        if (deleted) {
            return ResponseEntity.ok("Čas i sve njegove veze su uspešno obrisani.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Čas sa tim ID-em nije pronađen.");
        }
    }

    @PatchMapping("/updateCompleted/{id}")
    public ResponseEntity<PracticalClass> updateComplited(@PathVariable String id){
        return ResponseEntity.ok(practicalClassService.updateCompleted(id));
    }

}
