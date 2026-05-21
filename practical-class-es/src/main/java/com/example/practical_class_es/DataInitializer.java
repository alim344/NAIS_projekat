package com.example.practical_class_es;

import com.example.practical_class_es.doc.*;
import com.example.practical_class_es.repo.CandidateAnalyticsRepository;
import com.example.practical_class_es.repo.PracticalClassLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@Component
public class DataInitializer implements CommandLineRunner{

    @Autowired
    private CandidateAnalyticsRepository candidateAnalyticsRepository;

    @Autowired
    private PracticalClassLogRepository practicalClassLogRepository;

    private final Random random = new Random(42);

    // -- lookup arrays --
    private final String[] NAMES      = {"Ana","Marko","Jovana","Stefan","Milica","Nikola","Tijana","Luka","Ivana","Petar","Sara","Milan","Nina","Đorđe","Maja"};
    private final String[] LASTNAMES  = {"Jović","Nikolić","Petrović","Lukić","Savić","Marković","Ilić","Đorđević","Stanković","Popović","Vuković","Stojanović","Janković","Lazić","Pavlović"};
    private final String[] STATUSES   = {"PRACTICAL","PRACTICAL","PRACTICAL","ACTIVE","ACTIVE","PENDING","WAITING"};
    private final String[] CATEGORIES = {"A","B","B","B","C"};
    private final String[] LOCATIONS  = {"Novi Sad","Beograd","Niš","Kragujevac","Subotica"};
    private final String[] FLEX       = {"HIGH","HIGH","MEDIUM","LOW"};
    private final String[] DATES      = {"2025-06-10","2025-06-11","2025-06-12","2025-06-13","2025-06-14","2025-06-15","2025-06-16"};
    private final String[] START_TIMES= {"07:00","07:30","08:00","08:30","09:00","09:30","10:00","14:00","15:00","16:00"};
    private final String[] END_TIMES  = {"09:00","10:00","11:00","12:00","17:00","18:00","19:00"};
    private final String[] MODELS     = {"Golf","Polo","Passat","Astra","Corsa"};
    private final String[] NOTES      = {
            "Candidate handled parking and highway driving well",
            "Excellent city driving and roundabout handling",
            "Candidate had trouble with parking and reverse driving",
            "Good highway speed control but weak on city intersections",
            "Candidate panicked during highway entry needs more practice",
            "Smooth gear changes great awareness on roundabout",
            "Parking zone maneuver needs improvement city driving fine",
            "Bridge crossing and highway exit executed perfectly",
            "Reverse parking took too long but highway driving excellent",
            "Outstanding performance on all city and highway segments"
    };
    private final String[] ROUTES = {
            "Novi Sad city center highway exit parking zone",
            "Novi Sad city roundabout bridge highway",
            "Novi Sad parking zone reverse maneuver",
            "Novi Sad highway intersection city exit bridge",
            "Novi Sad highway entry exit",
            "Beograd city center bypass highway",
            "Novi Sad bridge exit roundabout parking",
            "Novi Sad industrial zone highway city center",
            "Novi Sad parking highway bridge city",
            "Beograd highway city roundabout exit"
    };

    @Override
    public void run(String... args) {

        // Pokreni samo ako su indeksi prazni
        if (candidateAnalyticsRepository.count() > 0
                && practicalClassLogRepository.count() > 0) {
            System.out.println(">>> DataInitializer: podaci već postoje, preskačem.");
            return;
        }

        System.out.println(">>> DataInitializer: ubacujem 1000 kandidata i 1000 časova...");

        // ── 1000 CandidateAnalytics ──────────────────────────────────────────
        List<CandidateAnalytics> candidates = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            CandidateAnalytics c = new CandidateAnalytics();
            c.setCandidateId((long) i);
            c.setName(pick(NAMES));
            c.setLastname(pick(LASTNAMES));
            c.setEmail(c.getName().toLowerCase() + "." + c.getLastname().toLowerCase() + i + "@gmail.com");
            c.setStartOfTraining("2025-0" + (1 + random.nextInt(6)) + "-" + String.format("%02d", 1 + random.nextInt(28)));
            c.setPreferredLocation(pick(LOCATIONS));
            c.setCategory(pick(CATEGORIES));

            // Raspoređujemo statusove tako da sva 3 upita imaju podatke
            String status = pick(STATUSES);
            c.setStatus(status);

            // theoryCompleted: PRACTICAL uvek true, ostali mešano
            c.setTheoryCompleted(status.equals("PRACTICAL") || random.nextBoolean());

            // avgClassGrade: PRACTICAL kandidati imaju veće ocene (za upit 1 >= 4.0)
            double grade = status.equals("PRACTICAL")
                    ? 3.5 + random.nextDouble() * 1.5   // 3.5 – 5.0
                    : 1.5 + random.nextDouble() * 3.5;  // 1.5 – 5.0
            c.setAvgClassGrade(Math.round(grade * 10.0) / 10.0);

            c.setTotalKmDriven(10 + random.nextInt(491));       // 10 – 500
            c.setNumberOfHeldClasses(1 + random.nextInt(30));   // 1 – 30

            // activePrefs — 1 do 3 nested preferencije
            int prefCount = 1 + random.nextInt(3);
            List<TimePrefInfo> prefs = new ArrayList<>();
            for (int p = 0; p < prefCount; p++) {
                TimePrefInfo pref = new TimePrefInfo();
                pref.setDate(pick(DATES));
                pref.setStartTime(pick(START_TIMES));
                pref.setEndTime(pick(END_TIMES));
                pref.setFlexibility(pick(FLEX));
                prefs.add(pref);
            }
            c.setActivePrefs(prefs);

            candidates.add(c);
        }
        candidateAnalyticsRepository.saveAll(candidates);
        System.out.println(">>> Upisano " + candidates.size() + " kandidata.");

        // ── 1000 PracticalClassLog ───────────────────────────────────────────
        List<PracticalClassLog> logs = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            PracticalClassLog log = new PracticalClassLog();
            log.setPracticalClassId((long) i);

            String date    = "2025-06-" + String.format("%02d", 1 + random.nextInt(30));
            int    startH  = 7 + random.nextInt(11);   // 07 – 17
            int    endH    = startH + 1 + random.nextInt(3);
            log.setStartTime(date + "T" + String.format("%02d", startH) + ":00");
            log.setEndTime(  date + "T" + String.format("%02d", endH)   + ":00");

            // completed: 70% true — za upit 1 i 2 koji filtriraju completed=true
            boolean completed = random.nextInt(10) < 7;
            log.setCompleted(completed);

            log.setKmDriven(5 + random.nextInt(96));            // 5 – 100
            log.setScore(1 + random.nextInt(5));                // 1 – 5
            log.setConsumedFuelLiters(2.0 + Math.round(random.nextDouble() * 80) / 10.0); // 2.0 – 10.0

            // malfunction: 15% true — za upit 3 (problematični)
            boolean malfunction = random.nextInt(10) < 2;

            log.setInstructorNote(pick(NOTES));
            log.setRoute(pick(ROUTES));

            InstructorInfo instructor = new InstructorInfo();
            instructor.setId((long) (1 + random.nextInt(5)));
            instructor.setName(pick(NAMES));
            instructor.setLastname(pick(LASTNAMES));
            log.setInstructorInfo(instructor);

            CandidateInfo candidate = new CandidateInfo();
            candidate.setId((long) (1 + random.nextInt(1000)));
            candidate.setName(pick(NAMES));
            candidate.setLastName(pick(LASTNAMES));
            candidate.setCategory(pick(CATEGORIES));
            log.setCandidateInfo(candidate);

            VehicleInfo vehicle = new VehicleInfo();
            vehicle.setId((long) (1 + random.nextInt(20)));
            vehicle.setModel(pick(MODELS));
            vehicle.setRegistration("NS-" + String.format("%03d", random.nextInt(1000)) + "-" + (char)('A'+random.nextInt(26)) + (char)('A'+random.nextInt(26)));
            vehicle.setMalfunction(malfunction);
            log.setVehicleInfo(vehicle);

            logs.add(log);
        }
        practicalClassLogRepository.saveAll(logs);
        System.out.println(">>> Upisano " + logs.size() + " časova.");
        System.out.println(">>> DataInitializer: gotovo!");
    }

    private String pick(String[] arr) {
        return arr[random.nextInt(arr.length)];
    }
}
