package com.example.theory_saga.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SagaResultDTO {
    private boolean success;
    private String message;
    private Long neo4jClassId;
    private String elasticsearchLogId;
}
