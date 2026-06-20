package com.example.class_organization.service;

import com.example.class_organization.dto.AddingCandidatesDTO;
import com.example.class_organization.dto.InstructorDTO;
import com.example.class_organization.model.Category;
import com.example.class_organization.model.Instructor;
import com.example.class_organization.model.Vehicle;
import com.example.class_organization.repo.InstructorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstructorService {

    @Autowired
    private InstructorRepository instructorRepository;



    public Instructor registerInstructor(InstructorDTO dto){

        Instructor instructor = new Instructor();
        instructor.setName(dto.getName());
        instructor.setEmail(dto.getEmail());
        instructor.setLastname(dto.getLastname());
        instructor.setPassword(dto.getPassword());
        instructor.setUsername(dto.getUsername());
        return instructorRepository.save(instructor);

    }

    public List<Instructor> getAll(){
        return instructorRepository.findAll();
    }


    public int deleteInstructor(String id){
        return instructorRepository.deleteInstructor(id);
    }

    public int addCandidate(AddingCandidatesDTO dto){
        String candidateUsername = dto.getC_username();
        String instructorUsername = dto.getI_username();
        return instructorRepository.createTrains(candidateUsername, instructorUsername);


    }

    public int deleteCandidate(AddingCandidatesDTO dto){
        String candidateUsername = dto.getC_username();
        String instructorUsername = dto.getI_username();
        return instructorRepository.deleteTrains(candidateUsername, instructorUsername);
    }




    public void createTeaching(String username, Long classId){

        try{

            int i = instructorRepository.createTeaches(username,classId,0," ");

            if(i == 0){
                throw new RuntimeException("Nije pronađen čas: ");
            }

        }catch(DataAccessException e){
            System.out.println("Greska u bayu"+e.getMessage());
        }
    }


    public void updateTeaching(String username, String classId, int score, String note){
        try{

            int i = instructorRepository.updateTeaches(username,classId,score,note);

            if(i == 0){
                throw new RuntimeException("Nije pronađen čas: ");
            }

        }catch(DataAccessException e){
            System.out.println("Greska u bayu"+e.getMessage());
        }


    }



    public List<Instructor> getInstructorReccomendations(String id){
        return instructorRepository.recommendInstructorsForCandidate(id);
    }


    public List<Instructor> getTopInstructors(int minClasses){
        return instructorRepository.findTopInstructorsByScore(minClasses);

    }

    public Instructor findByClassID(Long id){
        return instructorRepository.findByClassId(id).orElse(null);
    }

    public Vehicle findVehicleByInstId(Long id){
        return instructorRepository.findVehicleByInstructorId(id).orElse(null);
    }
}
