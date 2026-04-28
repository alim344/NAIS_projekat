package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Node("vehicle")
public class Vehicle {

    @Id
    @GeneratedValue
    private Long id;

    private String registrationNumber;

    private LocalDate registrationExpiryDate;

    private VehicleStatus status;

    private Integer currentMileage;

}
