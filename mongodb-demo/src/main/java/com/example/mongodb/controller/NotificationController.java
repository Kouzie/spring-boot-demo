package com.example.mongodb.controller;

import com.example.mongodb.model.notification.NotificationDocument;
import com.example.mongodb.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationService service;

    /**
     * curl -X GET http://localhost:8080/notification/userId/1
     */
    @GetMapping("/userId/{userId}")
    public List<NotificationDocument> getAllBuUserId(@PathVariable Long userId) {
        return service.getAll();
    }
}