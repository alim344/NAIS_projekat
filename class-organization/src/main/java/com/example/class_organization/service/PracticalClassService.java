package com.example.class_organization.service;

import com.example.class_organization.dto.PracticalClassDTO;
import com.example.class_organization.model.PracticalClass;
import com.example.class_organization.repo.PracticalClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
        if (dto.getEndTime() != null) {
            practicalClass.setEndTimeFromDateTime(dto.getEndTime());
        }

        if (dto.getStartTime() != null) {
            practicalClass.setStartTimeFromDateTime(dto.getStartTime());
        }
        PracticalClass pc = practicalClassRepository.save(practicalClass);

        candidateService.createAttendance(dto.getCandidate_username(),pc.getId());
        instructorService.createTeaching(dto.getInstructor_username(),pc.getId());

    }


    /*public List<PracticalClassDTO> getAllPracticalClasses() {

        List<Map<String,Object>> classes = practicalClassRepository.getAllPracticalClasses();
        List<PracticalClassDTO> practicalClassDTOList = new ArrayList<>();
        practicalClassDTOList = classes.stream().map(row -> {
            PracticalClassDTO dto = new PracticalClassDTO();
            String startStr = (String) row.get("startTime");
            String endStr = (String) row.get("endTime");


            if (startStr != null) {
                dto.setStartTime(LocalDateTime.parse(startStr.replace("Z", "")));
            }

            if (endStr != null) {
                dto.setEndTime(LocalDateTime.parse(endStr.replace("Z", "")));
            }
            dto.setCompleted((boolean) row.get("completed"));
            dto.setCandidate_username((String) row.get("candidate_username"));
            dto.setInstructor_username((String) row.get("instructor_username"));
            return dto;

        }).toList();

        return practicalClassDTOList;

    }*/


  public List<PracticalClass> getAllPracticalClasses() {
      return practicalClassRepository.findAll();
  }


    public boolean deleteClass(String id){

        int deletedCount = practicalClassRepository.deleteClassAndAllConnections(id);
        return deletedCount > 0;

    }

    public PracticalClass updateCompleted(String id){
        boolean completed = true;
        return practicalClassRepository.updateCompleted(id,completed);
    }



}
