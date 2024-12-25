package com.example.mongodb.model.alarm;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserAlarmRepository extends MongoRepository<UserAlarmDocument, String> {
}