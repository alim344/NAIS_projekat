package com.example.class_organization.service;

import com.example.class_organization.model.Instructor;
import com.example.class_organization.repo.InstructorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class InstructorService {

    @Autowired
    private InstructorRepository instructorRepository;

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
}
