package com.example.redis.controller;

import com.example.redis.model.UserData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user-hash")
public class UserHashRedisTemplateController {
    private final RedisTemplate<String, Object> jacksonRedisTemplate;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class ExpiringValue {
        private UserData userData;
        private Instant expiration;
    }

    private String getGroupHashKey(String group) {
        return "users-hash:group:" + group;
    }

    @GetMapping("/{group}")
    public List<UserData> getAllUserByGroupId(@PathVariable String group) {
        String groupHashKey = getGroupHashKey(group);
        log.info("getAllUserByGroupId invoked for hash key: {}", groupHashKey);

        Map<Object, Object> userEntries = jacksonRedisTemplate.opsForHash().entries(groupHashKey);
        if (userEntries.isEmpty()) {
            return new ArrayList<>();
        }

        List<UserData> validUsers = new ArrayList<>();
        List<String> expiredKeys = new ArrayList<>();

        for (Map.Entry<Object, Object> entry : userEntries.entrySet()) {
            ExpiringValue expiringValue = (ExpiringValue) entry.getValue();
            if (expiringValue.getExpiration().isAfter(Instant.now())) {
                validUsers.add(expiringValue.getUserData());
            } else {
                expiredKeys.add((String) entry.getKey());
            }
        }

        if (!expiredKeys.isEmpty()) {
            log.info("Deleting expired keys from hash {}: {}", groupHashKey, expiredKeys);
            jacksonRedisTemplate.opsForHash().delete(groupHashKey, expiredKeys.toArray());
        }

        return validUsers;
    }

    @PostMapping("/{group}")
    public UserData insert(@PathVariable String group) {
        UserData randomUser = UserData.randomUser();
        String groupHashKey = getGroupHashKey(group);

        Instant expirationTime = Instant.now().plus(10, ChronoUnit.SECONDS);
        ExpiringValue valueToStore = new ExpiringValue(randomUser, expirationTime);

        log.info("insert invoked for hash key: {}, field: {}, user: {}, expiration: {}", groupHashKey, randomUser.getUsername(), randomUser, expirationTime);
        jacksonRedisTemplate.opsForHash().put(groupHashKey, randomUser.getUsername(), valueToStore);
        return randomUser;
    }

    @DeleteMapping("/{group}")
    public void deleteAll(@PathVariable String group) {
        String groupHashKey = getGroupHashKey(group);
        log.info("deleteAll called for hash key: {}", groupHashKey);
        jacksonRedisTemplate.delete(groupHashKey);
    }

    @DeleteMapping("/{group}/{username}")
    public void deleteUser(@PathVariable String group, @PathVariable String username) {
        String groupHashKey = getGroupHashKey(group);
        log.info("deleteUser called for hash key: {}, field: {}", groupHashKey, username);
        jacksonRedisTemplate.opsForHash().delete(groupHashKey, username);
    }
}
