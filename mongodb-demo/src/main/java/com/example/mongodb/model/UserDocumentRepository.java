package com.example.mongodb.model;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserDocumentRepository extends MongoRepository<UserDocument, String> {
    // 커스텀 메서드 정의 예시
    UserDocument findByEmail(String email);
}