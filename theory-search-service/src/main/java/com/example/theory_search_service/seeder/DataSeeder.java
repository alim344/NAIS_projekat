package com.example.theory_search_service.seeder;

import com.example.theory_search_service.model.TheoryClassLog;
import com.example.theory_search_service.model.TheoryQuestion;
import com.example.theory_search_service.repo.TheoryClassLogRepository;
import com.example.theory_search_service.repo.TheoryQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final TheoryQuestionRepository questionRepository;
    private final TheoryClassLogRepository classLogRepository;

    private static final String[] CATEGORIES = {"signs", "rules", "safety", "vehicles", "highway"};
    private static final String[] PROFESSORS = {"prof", "prof2", "prof3", "prof4", "prof5"};
    private static final String[] PROFESSOR_NAMES = {
            "Petar Petrovic", "Jovana Jovanovic", "Marko Nikolic", "Milica Dordevic", "Dusan Stojanovic"
    };
    private static final String[] CLASSROOMS = {"Mala Sala 101", "Velika Sala Amfiteatar", "Srednja Sala 202"};
    private static final int[] CAPACITIES = {5, 50, 20};

    @Override
    public void run(String... args) {
        seedQuestions();
        seedClassLogs();
    }

    private void seedQuestions() {
        if (questionRepository.count() >= 1000) {
            System.out.println("Questions already seeded, skipping...");
            return;
        }

        System.out.println("Seeding theory questions...");
        List<TheoryQuestion> questions = new ArrayList<>();

        for (int lessonNum = 1; lessonNum <= 40; lessonNum++) {
            String lessonTitle = getLessonTitle(lessonNum);
            String category = CATEGORIES[lessonNum % CATEGORIES.length];

            for (int q = 1; q <= 25; q++) {
                TheoryQuestion question = new TheoryQuestion();
                question.setId(UUID.randomUUID().toString());
                question.setLessonOrderNumber(lessonNum);
                question.setLessonTitle(lessonTitle);
                question.setCategory(category);
                question.setDifficultyLevel((q % 3) + 1);
                question.setQuestionText("Question " + q + " about " + lessonTitle);
                question.setCorrectAnswer("Correct answer for Q" + q + " lesson " + lessonNum);
                question.setWrongAnswers(new String[]{
                        "Wrong answer A for Q" + q,
                        "Wrong answer B for Q" + q,
                        "Wrong answer C for Q" + q
                });
                questions.add(question);

                if (questions.size() >= 100) {
                    questionRepository.saveAll(questions);
                    questions.clear();
                }
            }
        }

        if (!questions.isEmpty()) questionRepository.saveAll(questions);
        System.out.println("Seeded " + questionRepository.count() + " questions.");
    }

    private void seedClassLogs() {
        if (classLogRepository.count() >= 1000) {
            System.out.println("Class logs already seeded, skipping...");
            return;
        }

        System.out.println("Seeding class logs...");
        Random random = new Random();
        List<TheoryClassLog> logs = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            int profIndex = i % PROFESSORS.length;
            int classroomIndex = i % CLASSROOMS.length;
            int lessonNum = (i % 40) + 1;
            int duration = 45 + random.nextInt(46);
            LocalDateTime start = LocalDateTime.now()
                    .minusDays(random.nextInt(180))
                    .withHour(8 + random.nextInt(10))
                    .withMinute(0);
            int capacity = CAPACITIES[classroomIndex];
            int candidates = 1 + random.nextInt(capacity);

            TheoryClassLog log = new TheoryClassLog();
            log.setId(UUID.randomUUID().toString());
            log.setProfessorUsername(PROFESSORS[profIndex]);
            log.setProfessorFullName(PROFESSOR_NAMES[profIndex]);
            log.setClassroomName(CLASSROOMS[classroomIndex]);
            log.setClassroomCapacity(capacity);
            log.setLessonTitle(getLessonTitle(lessonNum));
            log.setLessonOrderNumber(lessonNum);
            log.setCategory(CATEGORIES[lessonNum % CATEGORIES.length]);
            log.setStartTime(start);
            log.setEndTime(start.plusMinutes(duration));
            log.setDurationMinutes(duration);
            log.setCandidateCount(candidates);
            log.setAverageCandidateScore(50 + random.nextFloat() * 50);
            log.setFullyAttended(candidates == capacity);
            logs.add(log);

            if (logs.size() >= 100) {
                classLogRepository.saveAll(logs);
                logs.clear();
            }
        }

        if (!logs.isEmpty()) classLogRepository.saveAll(logs);
        System.out.println("Seeded " + classLogRepository.count() + " class logs.");
    }

    private String getLessonTitle(int orderNumber) {
        return switch (orderNumber) {
            case 1 -> "Introduction to traffic regulations";
            case 2 -> "Basic traffic rules";
            case 3 -> "Traffic signs - general";
            case 4 -> "Danger warning signs";
            case 5 -> "Mandatory signs";
            case 6 -> "Information signs";
            case 7 -> "Supplementary panels";
            case 8 -> "Traffic light signals";
            case 9 -> "Road markings";
            case 10 -> "Authorized traffic personnel";
            case 11 -> "Right of way at intersections";
            case 12 -> "Right of way at intersections - special cases";
            case 13 -> "Vehicle speed";
            case 14 -> "Distance between vehicles";
            case 15 -> "Overtaking";
            case 16 -> "Passing oncoming traffic";
            case 17 -> "Passing a stopped vehicle";
            case 18 -> "Turning";
            case 19 -> "Reversing";
            case 20 -> "Driving in a roundabout";
            case 21 -> "Stopping and parking";
            case 22 -> "Stopping and parking - restrictions";
            case 23 -> "Use of lights";
            case 24 -> "Use of audible and light signals";
            case 25 -> "Vehicle load";
            case 26 -> "Towing a disabled vehicle";
            case 27 -> "Pedestrians in traffic";
            case 28 -> "Cyclists in traffic";
            case 29 -> "Motorcyclists in traffic";
            case 30 -> "Special traffic participants";
            case 31 -> "Driving on the highway";
            case 32 -> "Driving in tunnels";
            case 33 -> "Driving in bad weather conditions";
            case 34 -> "Vehicle technical characteristics";
            case 35 -> "Active and passive vehicle safety";
            case 36 -> "Vehicle handling - basics";
            case 37 -> "Dangers of alcohol and drugs in traffic";
            case 38 -> "Driver fatigue and illness";
            case 39 -> "First aid in traffic";
            case 40 -> "Eco-driving and fuel efficiency";
            default -> "Unknown lesson " + orderNumber;
        };
    }
}
