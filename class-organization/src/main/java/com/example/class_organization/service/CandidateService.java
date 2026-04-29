package com.example.class_organization.service;

import com.example.class_organization.dto.AttendanceGetDTO;
import com.example.class_organization.dto.RegistrationDTO;
import com.example.class_organization.model.Attendance;
import com.example.class_organization.model.Candidate;
import com.example.class_organization.model.TrainingStatus;
import com.example.class_organization.repo.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class CandidateService {

    @Autowired
    private CandidateRepository candidateRepository;


   public Candidate registerCandidate(RegistrationDTO  dto){
       Candidate candidate = new Candidate();
       candidate.setName(dto.getName());
       candidate.setEmail(dto.getEmail());
       candidate.setPassword(dto.getPassword());
       candidate.setUsername(dto.getUsername());
       candidate.setLastname(dto.getLastname());
       candidate.setCategory(dto.getCategory());
       candidate.setStatus(dto.getStatus());
       candidate.setPreferredLocation(dto.getPreferredLocation());
       candidate.setTheoryCompleted(dto.isTheoryCompleted());
       candidate.setStartOfTrainingFromDateTime(dto.getStartOfTraining());
       return candidateRepository.save(candidate);
   }

    public int deleteCandidate(String id){
       return candidateRepository.deleteCandidate(id);
    }

    public List<Candidate> getAllCandidates(){
       return candidateRepository.findAll();
    }


    public Candidate updateCandidateStatus(String id, String status){


        try {
            TrainingStatus newStatus = TrainingStatus.valueOf(status.toUpperCase());
            return candidateRepository.updateStatus(id, newStatus);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Nepostojeći status: " + status);
        }

    }

    public Candidate updatePrefLocation(String id, String prefLocation){

        return candidateRepository.updatePrefLocation(id,prefLocation);
    }


    public int updateAttendance(String username, String classId, int km , boolean present ) {

            return candidateRepository.updateAttendanceById(username, classId, km, present);
    }


    public void createAttendance(String username, Long classId){
        try{
            boolean present = false;
            int i = candidateRepository.createAttendance(username, classId, 0, present );

            if(i == 0){
                throw new RuntimeException("Nije pronađen zakazan čas" );
            }

        }catch(DataAccessException e){
            System.out.println("Greska u bayu"+e.getMessage());
        }
    }

    /*public List<AttendanceGetDTO> getCandidateAttendance(String username){
        List<Attendance> attendances = candidateRepository.findAllAttendancesByUsername(username);
        return attendances.stream().map(a -> {
            AttendanceGetDTO dto = new AttendanceGetDTO();
            dto.setUsername(username);
            dto.setPresent(a.isPresent());
            dto.setKmDriven(a.getKmDriven());
            dto.setStartTime(a.getPracticalClass().getStartTimeAsDateTime());
            dto.setEndTime(a.getPracticalClass().getEndTimeAsDateTime());
            dto.setCompleted(a.getPracticalClass().isCompleted());
            return dto;
        }).toList();
    }*/

    public List<AttendanceGetDTO> getCandidateAttendance(String username) {
        Candidate candidate = candidateRepository.findByUsernameWithAttendances(username)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        return candidate.getAttendanceList().stream().map(a -> {
            AttendanceGetDTO dto = new AttendanceGetDTO();
            dto.setUsername(username);
            dto.setPresent(a.isPresent() != null && a.isPresent());
            dto.setKmDriven(a.getKmDriven());
            dto.setStartTime(a.getPracticalClass().getStartTimeAsDateTime());
            dto.setEndTime(a.getPracticalClass().getEndTimeAsDateTime());
            dto.setCompleted(a.getPracticalClass().isCompleted());
            return dto;
        }).toList();
    }



}
