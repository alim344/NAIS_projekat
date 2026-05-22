package com.example.theory_search_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void save(String key, Object value, long ttlMinutes) {
        redisTemplate.opsForValue().set(key, value, ttlMinutes, TimeUnit.MINUTES);
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void set(String key, Object value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl.getSeconds(), TimeUnit.SECONDS);
    }

    public void addToLeaderboard(String username, double score) {
        redisTemplate.opsForZSet().add("leaderboard:scores", username, score);
    }

    public Set<Object> getTopLeaderboard(int topN) {
        return redisTemplate.opsForZSet().reverseRange("leaderboard:scores", 0, topN - 1);
    }

    public Double getCandidateScore(String username) {
        return redisTemplate.opsForZSet().score("leaderboard:scores", username);
    }

    public void incrementActivityCount(String username) {
        redisTemplate.opsForHash().increment("activity:counts", username, 1);
    }

    public Object getActivityCount(String username) {
        return redisTemplate.opsForHash().get("activity:counts", username);
    }

    public Map<Object, Object> getAllActivityCounts() {
        return redisTemplate.opsForHash().entries("activity:counts");
    }

    public void setActiveLesson(String username, String lessonTitle) {
        redisTemplate.opsForValue().set("session:" + username + ":active-lesson",
                lessonTitle, 2, TimeUnit.HOURS);
    }

    public Object getActiveLesson(String username) {
        return redisTemplate.opsForValue().get("session:" + username + ":active-lesson");
    }

    public void pushRecentActivity(String username, String activity) {
        String key = "recent:" + username;
        redisTemplate.opsForList().leftPush(key, activity);
        redisTemplate.opsForList().trim(key, 0, 9);
    }

    public List<Object> getRecentActivities(String username) {
        return redisTemplate.opsForList().range("recent:" + username, 0, -1);
    }
}
