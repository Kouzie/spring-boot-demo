package com.example.mongodb.model.notification;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationRepository extends MongoRepository<NotificationDocument, String> {
    // 공통적으로 사용할 메서드 정의 가능
}