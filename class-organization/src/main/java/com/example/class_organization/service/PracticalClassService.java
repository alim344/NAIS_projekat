package com.example.class_organization.service;

import com.example.class_organization.config.RabbitConfig;
import com.example.class_organization.dto.CompletedDTO;
import com.example.class_organization.dto.PracticalClassDTO;
import com.example.class_organization.model.*;
import com.example.class_organization.repo.PracticalClassRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

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

    @Autowired
    private RabbitTemplate rabbitTemplate;



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


    //SAGA

    public void completeClass(Long classId, CompletedDTO req) {



        PracticalClass pc = practicalClassRepository.findByClassId(classId).orElse(null);

        if(pc == null){
            System.out.println("There is no practical class like that sorrz error");
            return;
        }

        pc.setCompleted(true);
        practicalClassRepository.save(pc);

        Candidate candidate = candidateService.getById(req.getCandidateId());

        if(candidate == null){
            System.out.println("There is no candidte under this id");
            return;
        }

        candidateService.updateAttendanceByIds(req.getCandidateId(),classId, true, req.getKmDriven());

        Instructor instructor = instructorService.findByClassID(classId);


        CompletedDTO dto = new CompletedDTO();
        dto.setPracticalClassId(classId);
        dto.setCandidateId(req.getCandidateId());
        dto.setKmDriven(req.getKmDriven());
        dto.setScore(req.getScore());
        dto.setStartTime(pc.getStartTime());
        dto.setEndTime(pc.getEndTime());
        dto.setInstructorNote(req.getInstructorNote());
        dto.setCand_lastName(candidate.getLastname());
        dto.setCand_name(candidate.getName());
        dto.setCategory(candidate.getCategory().toString());
        dto.setConsumedFuelLiters(req.getConsumedFuelLiters());


        if (instructor != null) {
            dto.setInst_name(instructor.getName());
            dto.setInst_lastName(instructor.getLastname());

            Vehicle vehicle = instructorService.findVehicleByInstId(instructor.getId());
            if (vehicle != null) {
                dto.setRegistration(vehicle.getRegistrationNumber());
                dto.setMalfunction(vehicle.getStatus() == VehicleStatus.OUT_OF_SERVICE);
            } else {
                dto.setMalfunction(false);
            }
        } else {
            dto.setMalfunction(false);
        }



        System.out.println("Slanje poruke na RabbitMQ...-----------------------------------------------------*");
        rabbitTemplate.convertAndSend(
                RabbitConfig.CLASS_EVENTS_EXCHANGE,
                "class.completed",
                dto
        );
    }

    @RabbitListener(queues = RabbitConfig.ROLLBACK_QUEUE)
    public void handleRollback(CompletedDTO event) {
        PracticalClass pc = practicalClassRepository
                .findById(event.getPracticalClassId())
                .orElse(null);

        if (pc != null) {
            pc.setCompleted(false);
            practicalClassRepository.save(pc);
            System.out.println("ROLLBACK: class  not documented");
        }

        Candidate candidate = candidateService.getById(event.getCandidateId());
        /*if (candidate != null) {
            candidate.getAttendanceList().stream()
                    .filter(a -> a.getPracticalClass().getId().equals(event.getPracticalClassId()))
                    .findFirst()
                    .ifPresent(a -> {
                        a.setPresent(false);
                        a.setKmDriven(0);
                    });
            candidateService.save(candidate);*/
        candidateService.updateAttendanceByIds(event.getCandidateId(), event.getPracticalClassId(), false, 0);

    }
}





