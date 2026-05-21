package com.example.class_organization.controller;

import com.example.class_organization.dto.TimePreferenceDTO;
import com.example.class_organization.model.TimePreference;
import com.example.class_organization.service.TimePreferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/timepref")
public class TimePreferenceController {

    @Autowired
    private TimePreferenceService timePreferenceService;

    @GetMapping("/getAll")
    public ResponseEntity<List<TimePreference>> getAll() {
        return ResponseEntity.ok(timePreferenceService.getAll());
    }

    @PostMapping("/create")
    public ResponseEntity<TimePreference> create(@RequestBody TimePreferenceDTO dto) {
        return ResponseEntity.ok(timePreferenceService.createTimePreference(dto));
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable String id) {
        timePreferenceService.deleteTimePreference(id);
    }

    @PatchMapping("/update/{id}")
    public TimePreference update(@RequestBody TimePreferenceDTO dto, @PathVariable String id) {
        return timePreferenceService.updateTimePreference(dto, id);
    }

}
