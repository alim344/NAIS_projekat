package model;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@RelationshipProperties
public class Drives {

    @RelationshipId
    private Long id;

    private LocalDate assignedDate;

    private Integer mileageAtAssignment;

    @TargetNode
    private Vehicle vehicle;
}
