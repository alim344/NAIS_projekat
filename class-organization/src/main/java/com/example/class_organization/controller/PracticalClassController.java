package com.example.class_organization.controller;

import com.example.class_organization.dto.PracticalClassDTO;
import com.example.class_organization.service.PracticalClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
