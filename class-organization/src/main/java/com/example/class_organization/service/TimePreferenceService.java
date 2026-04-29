package com.example.class_organization.service;

import com.example.class_organization.dto.TimePreferenceDTO;
import com.example.class_organization.model.TimePreference;
import com.example.class_organization.repo.TimePreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TimePreferenceService {

    @Autowired
    private TimePreferenceRepository timePreferenceRepository;
    @Autowired
    private CandidateService candidateService;


    public List<TimePreference> getAll(){
        return timePreferenceRepository.findAll();
    }

    public TimePreference createTimePreference(TimePreferenceDTO dto){

        TimePreference timePreference = new TimePreference();
        timePreference.setEndTime(dto.getEndTime());
        timePreference.setStartTime(dto.getStartTime());
        timePreference.setDate(dto.getDate());
        TimePreference tp =   timePreferenceRepository.save(timePreference);

        candidateService.makeHasPref(dto.getC_username(), tp.getId());
        return tp;


    }

    public void deleteTimePreference(String id){
        timePreferenceRepository.deletePref(id);
    }

    public TimePreference updateTimePreference(TimePreferenceDTO dto, String id){

        String startTime = dto.getStartTime();
        String endTime = dto.getEndTime();
        String dateOfPref = dto.getDate();


        return timePreferenceRepository.updatePref(startTime,endTime,dateOfPref,id);
    }




}
