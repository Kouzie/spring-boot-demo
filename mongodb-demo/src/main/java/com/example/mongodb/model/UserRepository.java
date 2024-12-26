package com.example.mongodb.model;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UserRepository extends MongoRepository<UserDocument, String> {
    // 커스텀 메서드 정의 예시
    UserDocument findByEmail(String email);

    // org.springframework.data.mongodb.repository.Query
    // 특정 조건을 추가한 커스텀 쿼리 예시
    @Query("{ 'username' : ?0, 'email' : ?1 }")
    UserDocument findByUsernameAndEmail(String username, String email);
}