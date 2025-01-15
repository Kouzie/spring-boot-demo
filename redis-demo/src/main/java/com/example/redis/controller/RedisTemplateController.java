package com.example.redis.controller;

import com.example.redis.model.UserData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/redis-template")
public class RedisTemplateController {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, Object> jacksonRedisTemplate;

    @PostMapping("/normal")
    public void addUserData(@RequestBody UserData userData) {
        String key = userData.getUsername();
        redisTemplate.opsForValue().set(key, userData);
    }

    @GetMapping("/normal/{username}")
    public UserData getUserData(@PathVariable String username) {
        return (UserData) redisTemplate.opsForValue().get(username);
    }

    @PostMapping("/jackson")
    public void addUserDataJackson(@RequestBody UserData userData) {
        String key = userData.getUsername();
        jacksonRedisTemplate.opsForValue().set(key, userData);
    }

    @GetMapping("/jackson/{username}")
    public UserData getUserDataJackson(@PathVariable String username) {
        return (UserData) jacksonRedisTemplate.opsForValue().get(username);
    }
}

