package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Node("documents")
public class InstructorDocuments {

    @Id
    @GeneratedValue
    private Long id;

    private String documentType;

    private LocalDate expiryDate;

}
