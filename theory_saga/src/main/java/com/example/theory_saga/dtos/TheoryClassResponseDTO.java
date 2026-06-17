package com.example.theory_saga.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class TheoryClassResponseDTO {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
