package com.example.class_organization.controller;

import com.example.class_organization.dto.TeachingDTO;
import com.example.class_organization.model.Instructor;
import com.example.class_organization.service.InstructorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/instructor")
public class InstructorController {

    @Autowired
    private InstructorService instructorService;

    @PatchMapping("/updateTeaching")
    public void updateTeaching(@RequestBody TeachingDTO dto) {
        instructorService.updateTeaching(dto.getUsername(),dto.getClassId(),dto.getScore(), dto.getNote());
    }




}
