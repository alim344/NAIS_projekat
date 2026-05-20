package com.example.class_organization.repo;

import com.example.class_organization.model.TimePreference;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

public interface TimePreferenceRepository extends Neo4jRepository<TimePreference, Long> {

    @Query("MATCH (t:TimePreference) WHERE elementId(t) = $id " +
            "DETACH DELETE t " +
            "RETURN count(*)")
    int deletePref(String id);

    @Query("MATCH (t:TimePreference) WHERE elementId(t) = $id " +
            "SET t.startTime = $startTime, t.endTime = $endTime, t.date = $dateOfPref " +
            "RETURN t")
    TimePreference updatePref(String startTime,String endTime,String dateOfPref,String id);

}
