package com.example.mongodb.controller;

import com.example.mongodb.controller.dto.CreateUserRequestDto;
import com.example.mongodb.model.alarm.UserAlarmDocument;
import com.example.mongodb.model.UserDocument;
import com.example.mongodb.service.UserAlarmService;
import com.example.mongodb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserAlarmService userAlarmService;

    @GetMapping("/{id}")
    public UserDocument getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public UserDocument createUser(@RequestBody CreateUserRequestDto user) {
        return userService.createUser(user);
    }

    @PutMapping("/{id}")
    public UserDocument updateUser(@PathVariable String id, @RequestBody CreateUserRequestDto user) {
        return userService.updateUserBySession3(id, user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
    }

    /**
     * username과 email로 동적 사용자 조회
     */
    @GetMapping("/search")
    public UserDocument getUserByParam(@RequestParam String username,
                                       @RequestParam String email) {
        // List<UserDocument> users = userService.getUserByParam(username, email);
        UserDocument user = userService.getUserByUsernameAndEmail(username, email);
        return user;
    }

    /**
     * username이 특정 문자로 시작하는 사용자 조회
     */
    @GetMapping("/search/prefix")
    public List<UserDocument> getUsersByUsernamePrefix(
            @RequestParam String prefix
    ) {
        List<UserDocument> users = userService.getUsersByUsernamePrefix(prefix);
        return users;
    }

    // curl -X GET "http://localhost:8080/users/alarm/USER-123"
    @GetMapping("/alarm/{userId}")
    public UserAlarmDocument addAlarm(@PathVariable String userId) {
        return userAlarmService.getUserAlarmByUserId(userId);
    }
}