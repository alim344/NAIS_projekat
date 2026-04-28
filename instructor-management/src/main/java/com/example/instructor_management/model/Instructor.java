package model;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Node("instructor")
public class Instructor extends User {

    private Integer maxCapacity;

    @Relationship(type="DRIVES", direction = Relationship.Direction.OUTGOING)
    private Vehicle vehicle;

    @Relationship(type="HAS_DOCUMENT", direction = Relationship.Direction.OUTGOING)
    private List<InstructorDocuments> documents = new ArrayList<>();

    @Relationship(type = "ASSIGNED_TO", direction = Relationship.Direction.OUTGOING)
    private List<Candidate> candidates = new ArrayList<>();

}
