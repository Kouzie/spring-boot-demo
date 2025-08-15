package com.example.redis.service;

import com.example.redis.model.UserData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCacheService {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    private String getZSetKeyForGroup(String group) {
        return "user:ttl_zset:" + group;
    }

    public List<UserData> getAllUsersByGroup(String group) {
        String zsetKey = getZSetKeyForGroup(group);
        double currentTime = System.currentTimeMillis();

        Set<String> validKeys = stringRedisTemplate.opsForZSet().rangeByScore(zsetKey, currentTime, Double.MAX_VALUE);
        if (validKeys == null || validKeys.isEmpty()) {
            return List.of();
        }

        List<String> resultsInJson = stringRedisTemplate.opsForValue().multiGet(validKeys);
        return resultsInJson.stream()
                .map(json -> {
                    try {
                        return objectMapper.readValue(json, UserData.class);
                    } catch (JsonProcessingException e) {
                        log.error("Failed to deserialize UserData from JSON", e);
                        return null;
                    }
                })
                .collect(Collectors.toList());
    }

    public UserData insertUser(String group) {
        String zsetKey = getZSetKeyForGroup(group);
        double currentTime = System.currentTimeMillis();

        // Find expired keys
        Set<String> expiredKeys = stringRedisTemplate.opsForZSet().rangeByScore(zsetKey, 0, currentTime);

        if (expiredKeys != null && !expiredKeys.isEmpty()) {
            log.info("Deleting {} expired user(s) from group {}", expiredKeys.size(), group);
            // Delete the actual user data
            stringRedisTemplate.delete(expiredKeys);
            // Remove from the sorted set
            stringRedisTemplate.opsForZSet().removeRangeByScore(zsetKey, 0, currentTime);
        }

        UserData randomUser = UserData.randomUser();
        String key = "user:" + group + ":" + randomUser.getUsername();

        long expirationTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(10);

        try {
            String userDataJson = objectMapper.writeValueAsString(randomUser);
            stringRedisTemplate.opsForValue().set(key, userDataJson, 10, TimeUnit.MINUTES);
            stringRedisTemplate.opsForZSet().add(zsetKey, key, expirationTime);
            return randomUser;
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize UserData to JSON", e);
            throw new RuntimeException(e);
        }
    }

    public void deleteAllUsersInGroup(String group) {
        String zsetKey = getZSetKeyForGroup(group);
        Set<String> allKeys = stringRedisTemplate.opsForZSet().range(zsetKey, 0, -1);

        if (allKeys != null && !allKeys.isEmpty()) {
            stringRedisTemplate.delete(allKeys);
        }
        stringRedisTemplate.delete(zsetKey);
    }

    public void deleteUser(String group, String username) {
        String key = "user:" + group + ":" + username;
        String zsetKey = getZSetKeyForGroup(group);

        stringRedisTemplate.delete(key);
        stringRedisTemplate.opsForZSet().remove(zsetKey, key);
    }
}
