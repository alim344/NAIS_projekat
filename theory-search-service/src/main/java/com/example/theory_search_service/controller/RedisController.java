package com.example.theory_search_service.controller;

import com.example.theory_search_service.service.RedisCacheService;
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

    private final RedisCacheService redisCacheService;

    @PostMapping("/set")
    public ResponseEntity<String> set(@RequestParam String key,
                                      @RequestParam String value,
                                      @RequestParam(defaultValue = "60") long ttlSeconds) {
        redisCacheService.set(key, value, Duration.ofSeconds(ttlSeconds));
        return ResponseEntity.ok("Saved: " + key);
    }

    @GetMapping("/get")
    public ResponseEntity<Object> get(@RequestParam String key) {
        Object value = redisCacheService.get(key);
        return value != null ? ResponseEntity.ok(value) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestParam String key) {
        redisCacheService.delete(key);
        return ResponseEntity.ok("Deleted: " + key);
    }

    @PostMapping("/leaderboard")
    public ResponseEntity<String> addScore(@RequestParam String username,
                                           @RequestParam double score) {
        redisCacheService.addToLeaderboard(username, score);
        return ResponseEntity.ok("Score added for: " + username);
    }

    @GetMapping("/leaderboard/top")
    public ResponseEntity<Set<Object>> getTopLeaderboard(
            @RequestParam(defaultValue = "10") int topN) {
        return ResponseEntity.ok(redisCacheService.getTopLeaderboard(topN));
    }

    @GetMapping("/leaderboard/{username}")
    public ResponseEntity<Double> getCandidateScore(@PathVariable String username) {
        Double score = redisCacheService.getCandidateScore(username);
        return score != null ? ResponseEntity.ok(score) : ResponseEntity.notFound().build();
    }

    @PostMapping("/activity/{username}/increment")
    public ResponseEntity<String> incrementActivity(@PathVariable String username) {
        redisCacheService.incrementActivityCount(username);
        return ResponseEntity.ok("Incremented for: " + username);
    }

    @GetMapping("/activity/{username}/count")
    public ResponseEntity<Object> getActivityCount(@PathVariable String username) {
        return ResponseEntity.ok(redisCacheService.getActivityCount(username));
    }

    @GetMapping("/activity/all")
    public ResponseEntity<Map<Object, Object>> getAllCounts() {
        return ResponseEntity.ok(redisCacheService.getAllActivityCounts());
    }

    @PostMapping("/session/{username}/lesson")
    public ResponseEntity<String> setActiveLesson(@PathVariable String username,
                                                  @RequestParam String lesson) {
        redisCacheService.setActiveLesson(username, lesson);
        return ResponseEntity.ok("Active lesson set for: " + username);
    }

    @GetMapping("/session/{username}/lesson")
    public ResponseEntity<Object> getActiveLesson(@PathVariable String username) {
        Object lesson = redisCacheService.getActiveLesson(username);
        return lesson != null ? ResponseEntity.ok(lesson) : ResponseEntity.notFound().build();
    }

    @PostMapping("/recent/{username}")
    public ResponseEntity<String> pushActivity(@PathVariable String username,
                                               @RequestParam String activity) {
        redisCacheService.pushRecentActivity(username, activity);
        return ResponseEntity.ok("Activity pushed for: " + username);
    }

    @GetMapping("/recent/{username}")
    public ResponseEntity<List<Object>> getRecent(@PathVariable String username) {
        return ResponseEntity.ok(redisCacheService.getRecentActivities(username));
    }
}
