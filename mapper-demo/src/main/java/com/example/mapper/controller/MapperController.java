package com.example.mapper.controller;

import com.example.mapper.request.notify.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mapper")
@RequiredArgsConstructor
public class MapperController {

    @GetMapping
    public List<Notification> getNotification() {
        List<Notification> notifications = new ArrayList<>();
        notifications.add(new Notification.MessageNotification(
                "60d5dbf87f3e6e3b2f4b2b3a", "message", "1", "You have a new message from Alice", Instant.parse("2023-08-01T10:00:00Z"), 2L
        ));
        notifications.add(new Notification.FriendRequestNotification(
                "60d5dbf87f3e6e3b2f4b2b3b", "friend_request", "1", "Bob sent you a friend request", Instant.parse("2023-08-01T10:05:00Z"), 3L
        ));
        notifications.add(new Notification.EventInviteNotification(
                "60d5dbf87f3e6e3b2f4b2b3c", "event_invite", "1", "You are invited to Sarah's birthday party", Instant.parse("2023-08-01T10:10:00Z"), 5L, "123 Main St"
        ));
        return notifications;
    }

    @PostMapping
    public void sendNotification(@RequestBody List<Notification> notifications) {
        // 받은 notifications를 처리하는 로직 추가
        notifications.forEach(notification -> {
            // 예시로 각 알림의 'type'을 출력하는 로직
            log.info("Received notification of type: " + notification.getNotificationType());
            log.info(notification.toString());
        });
    }
}

/*
curl -X GET http://localhost:8080/mapper

curl -X POST http://localhost:8080/mapper \
-H "Content-Type: application/json" \
-d '[
    {
        "id": "60d5dbf87f3e6e3b2f4b2b3a",
        "notificationType": "message",
        "userId": "1",
        "message": "You have a new message from Alice",
        "timestamp": "2023-08-01T10:00:00Z",
        "senderId": 2
    },
    {
        "id": "60d5dbf87f3e6e3b2f4b2b3b",
        "notificationType": "friend_request",
        "userId": "1",
        "message": "Bob sent you a friend request",
        "timestamp": "2023-08-01T10:05:00Z",
        "requesterId": 3
    },
    {
        "id": "60d5dbf87f3e6e3b2f4b2b3c",
        "notificationType": "event_invite",
        "userId": "1",
        "message": "You are invited to Sarah birthday party",
        "timestamp": "2023-08-01T10:10:00Z",
        "eventId": 5,
        "location": "123 Main St"
    }
]'
*/