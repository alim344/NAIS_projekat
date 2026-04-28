package com.example.class_organization.service;

import com.example.class_organization.dto.RegistrationDTO;
import com.example.class_organization.model.Candidate;
import com.example.class_organization.repo.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

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
       candidate.setStartOfTraining(dto.getStartOfTraining());
       return candidateRepository.save(candidate);
   }

    public void updateAttendance(String username, Long classId, int km , String note ) {
        try{

            int i = candidateRepository.updateAttendanceById(username, classId, km, note);

            if(i == 0){
                throw new RuntimeException("Nije pronađen zakazan čas za korisnika: " + username);
            }

        }catch(DataAccessException e){
            System.out.println("Greska u bayu"+e.getMessage());
        }
    }


    public void createAttendance(String username, Long classId){
        try{

            int i = candidateRepository.createAttendance(username, classId, 0, " ");

            if(i == 0){
                throw new RuntimeException("Nije pronađen zakazan čas" );
            }

        }catch(DataAccessException e){
            System.out.println("Greska u bayu"+e.getMessage());
        }
    }

}
