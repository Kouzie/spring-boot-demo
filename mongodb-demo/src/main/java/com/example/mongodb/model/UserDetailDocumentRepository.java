package com.example.mongodb.model;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserDetailDocumentRepository extends MongoRepository<UserDetailDocument, String> {
}