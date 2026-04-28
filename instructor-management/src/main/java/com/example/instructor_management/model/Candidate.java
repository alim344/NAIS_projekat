package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Node;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@Node("candidate")
public class Candidate extends User {

    private boolean theoryCompleted;

    private Category category;

    private TrainingStatus trainingStatus;
}
