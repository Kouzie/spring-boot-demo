package com.example.redis.controller;

import com.example.redis.model.UserData;
import com.example.redis.service.UserCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserRedisTemplateController {

    private final UserCacheService userCacheService;

    @GetMapping("/{group}")
    public List<UserData> getAllUserByGroupId(@PathVariable String group) {
        return userCacheService.getAllUsersByGroup(group);
    }

    @PostMapping("/{group}")
    public UserData insert(@PathVariable String group) {
        return userCacheService.insertUser(group);
    }

    @DeleteMapping("/{group}")
    public void deleteAll(@PathVariable String group) {
        userCacheService.deleteAllUsersInGroup(group);
    }

    @DeleteMapping("/{group}/{username}")
    public void deleteUser(@PathVariable String group, @PathVariable String username) {
        userCacheService.deleteUser(group, username);
    }
}
