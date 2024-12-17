package com.example.mongodb.controller;

import com.example.mongodb.controller.dto.CreateUserRequestDto;
import com.example.mongodb.model.UserDocument;
import com.example.mongodb.service.UserDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserDocumentService userDocumentService;

    @GetMapping
    public List<UserDocument> getAllUsers() {
        return userDocumentService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDocument getUserById(@PathVariable String id) {
        return userDocumentService.getUserById(id);
    }

    @PostMapping
    public UserDocument createUser(@RequestBody CreateUserRequestDto user) {
        return userDocumentService.createUser(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
        userDocumentService.deleteUser(id);
    }

    /**
     * username과 email로 동적 사용자 조회
     */
    @GetMapping("/search")
    public List<UserDocument> getUserByParam(@RequestParam String username,
                                             @RequestParam(required = false) String email
    ) {
        List<UserDocument> users = userDocumentService.getUserByParam(username, email);
        return users;
    }

    /**
     * username이 특정 문자로 시작하는 사용자 조회
     */
    @GetMapping("/search/prefix")
    public List<UserDocument> getUsersByUsernamePrefix(
            @RequestParam String prefix
    ) {
        List<UserDocument> users = userDocumentService.getUsersByUsernamePrefix(prefix);
        return users;
    }
}