package com.example.theory_organization.controller;

import com.example.theory_organization.model.Classroom;
import com.example.theory_organization.service.ClassroomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classrooms")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomService classroomService;

    @GetMapping
    public List<Classroom> getAll() {
        return classroomService.getAllClassrooms();
    }

    @PutMapping("/{id}")
    public Classroom update(@PathVariable Long id, @RequestBody Classroom classroom) {
        return classroomService.update(id, classroom);
    }
}
