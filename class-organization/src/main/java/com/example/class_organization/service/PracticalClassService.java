package com.example.class_organization.service;

import com.example.class_organization.dto.PracticalClassDTO;
import com.example.class_organization.model.PracticalClass;
import com.example.class_organization.repo.PracticalClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class PracticalClassService {

    @Autowired
    private PracticalClassRepository practicalClassRepository;

    @Autowired
    private CandidateService candidateService;
    @Autowired
    private InstructorService instructorService;


    @Transactional
    public void createClass(PracticalClassDTO dto) {

        PracticalClass practicalClass = new PracticalClass();
        practicalClass.setCompleted(false);
        practicalClass.setEndTime(dto.getEndTime());
        practicalClass.setStartTime(dto.getStartTime());
        PracticalClass pc = practicalClassRepository.save(practicalClass);

        candidateService.createAttendance(dto.getCandidate_username(),pc.getId());
        instructorService.createTeaching(dto.getInstructor_username(),pc.getId());

    }
}
