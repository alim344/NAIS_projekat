package com.example.theory_search_service.controller;

import com.example.theory_search_service.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/redis")
@RequiredArgsConstructor
public class RedisController {

    private final RedisService redisService;

    @PostMapping("/set")
    public ResponseEntity<String> set(@RequestParam String key,
                                      @RequestParam String value,
                                      @RequestParam(defaultValue = "60") long ttlSeconds) {
        redisService.set(key, value, Duration.ofSeconds(ttlSeconds));
        return ResponseEntity.ok("Saved: " + key);
    }

    @GetMapping("/get")
    public ResponseEntity<Object> get(@RequestParam String key) {
        Object value = redisService.get(key);
        return value != null ? ResponseEntity.ok(value) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestParam String key) {
        redisService.delete(key);
        return ResponseEntity.ok("Deleted: " + key);
    }

    @PostMapping("/leaderboard")
    public ResponseEntity<String> addScore(@RequestParam String username,
                                           @RequestParam double score) {
        redisService.addToLeaderboard(username, score);
        return ResponseEntity.ok("Score added for: " + username);
    }

    @GetMapping("/leaderboard/top")
    public ResponseEntity<Set<Object>> getTopLeaderboard(
            @RequestParam(defaultValue = "10") int topN) {
        return ResponseEntity.ok(redisService.getTopLeaderboard(topN));
    }

    @GetMapping("/leaderboard/{username}")
    public ResponseEntity<Double> getCandidateScore(@PathVariable String username) {
        Double score = redisService.getCandidateScore(username);
        return score != null ? ResponseEntity.ok(score) : ResponseEntity.notFound().build();
    }

    @PostMapping("/activity/{username}/increment")
    public ResponseEntity<String> incrementActivity(@PathVariable String username) {
        redisService.incrementActivityCount(username);
        return ResponseEntity.ok("Incremented for: " + username);
    }

    @GetMapping("/activity/{username}/count")
    public ResponseEntity<Object> getActivityCount(@PathVariable String username) {
        return ResponseEntity.ok(redisService.getActivityCount(username));
    }

    @GetMapping("/activity/all")
    public ResponseEntity<Map<Object, Object>> getAllCounts() {
        return ResponseEntity.ok(redisService.getAllActivityCounts());
    }

    @PostMapping("/session/{username}/lesson")
    public ResponseEntity<String> setActiveLesson(@PathVariable String username,
                                                  @RequestParam String lesson) {
        redisService.setActiveLesson(username, lesson);
        return ResponseEntity.ok("Active lesson set for: " + username);
    }

    @GetMapping("/session/{username}/lesson")
    public ResponseEntity<Object> getActiveLesson(@PathVariable String username) {
        Object lesson = redisService.getActiveLesson(username);
        return lesson != null ? ResponseEntity.ok(lesson) : ResponseEntity.notFound().build();
    }

    @PostMapping("/recent/{username}")
    public ResponseEntity<String> pushActivity(@PathVariable String username,
                                               @RequestParam String activity) {
        redisService.pushRecentActivity(username, activity);
        return ResponseEntity.ok("Activity pushed for: " + username);
    }

    @GetMapping("/recent/{username}")
    public ResponseEntity<List<Object>> getRecent(@PathVariable String username) {
        return ResponseEntity.ok(redisService.getRecentActivities(username));
    }
}
