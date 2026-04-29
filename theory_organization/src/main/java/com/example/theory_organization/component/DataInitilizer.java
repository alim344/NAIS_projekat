package com.example.theory_organization.component;

import com.example.theory_organization.model.Candidate;
import com.example.theory_organization.model.Classroom;
import com.example.theory_organization.model.Professor;
import com.example.theory_organization.model.TheoryLesson;
import com.example.theory_organization.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitilizer implements CommandLineRunner {

    private final CandidateService candidateService;
    private final ProfessorService professorService;
    private final TheoryClassService theoryClassService;
    private final ClassroomService classroomService;
    private final TheoryLessonService theoryLessonService;

    @Override
    public void run(String... args) throws Exception {
        // Proveravamo da li je baza prazna da ne bismo duplirali podatke pri svakom paljenju
        if (classroomService.getAllClassrooms().isEmpty()) {

            // 1. KREIRANJE UČIONICE (Koristi .save(), a ne .update())
            Classroom sala = new Classroom();
            sala.setName("Sala 101");
            sala.setCapacity(2);
            classroomService.save(sala); // Ovde je bila greška - promenjeno sa update na save

            // 2. KREIRANJE LEKCIJE
            TheoryLesson l1 = new TheoryLesson();
            l1.setTitle("Uvod u saobraćaj");
            l1.setOrderNumber(1);
            theoryLessonService.save(l1);

            // 3. KREIRANJE PROFESORA
            Professor prof = new Professor();
            prof.setName("Marko");
            prof.setLastname("Marković");
            prof.setAcademicTitle("Master inženjer");
            professorService.save(prof);

            // 4. KREIRANJE KANDIDATA
            Candidate c = new Candidate();
            c.setName("Sara");
            c.setUsername("sara123");
            c.setTheoryCompleted(false);
            candidateService.save(c);

            System.out.println(">> Osnovni podaci su uspešno inicijalizovani u Neo4j bazi.");
        } else {
            System.out.println(">> Podaci već postoje u bazi, inicijalizacija preskočena.");
        }
    }
}
