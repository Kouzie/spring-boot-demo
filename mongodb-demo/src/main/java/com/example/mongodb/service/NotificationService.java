package com.example.mongodb.service;

import com.example.mongodb.model.notification.NotificationDocument;
import com.example.mongodb.model.notification.NotificationRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

import static com.example.mongodb.model.notification.NotificationDocument.*;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MongoTemplate mongoTemplate;

    @PostConstruct
    public void saveNotifications() {
        // 메시지 알림
        NotificationDocument messageNotification = new MessageNotification(
                "msg-001", // id
                "message", // type
                1001L,     // userId
                "You have a new message.", // message
                Instant.now(), // timestamp
                2001L      // senderId
        );

        // 친구 요청 알림
        NotificationDocument friendRequestNotification = new FriendRequestNotification(
                "fr-001",  // id
                "friend_request", // type
                1002L,     // userId
                "John Doe sent you a friend request.", // message
                Instant.now(), // timestamp
                3001L      // requesterId
        );

        // 이벤트 초대 알림
        NotificationDocument eventInviteNotification = new EventInviteNotification(
                "evt-001", // id
                "event_invite", // type
                1003L,     // userId
                "You are invited to the Annual Meetup.", // message
                Instant.now(), // timestamp
                4001L,     // eventId
                "New York City" // location
        );

        // 저장
        notificationRepository.save(messageNotification);
        notificationRepository.save(friendRequestNotification);
        notificationRepository.save(eventInviteNotification);
    }

    public List<NotificationDocument> getAll() {
        return notificationRepository.findAll();
    }
}