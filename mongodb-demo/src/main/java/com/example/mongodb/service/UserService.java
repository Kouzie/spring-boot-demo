package com.example.mongodb.service;

import com.example.mongodb.controller.dto.CreateUserRequestDto;
import com.example.mongodb.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserDetailDocumentRepository userDetailRepository;
    private final MongoTemplate mongoTemplate;


    public List<UserDocument> getAllUsers() {
        return mongoTemplate.findAll(UserDocument.class);
    }

    public UserDocument getUserById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public UserDocument createUser(CreateUserRequestDto requestDto) {
        UserDocument user = userRepository.save(new UserDocument(requestDto.getUsername(), requestDto.getEmail()));
        // if (true) throw new IllegalArgumentException(""); // 트랜잭션 확인용
        UserDetailDocument userDetail = userDetailRepository.save(new UserDetailDocument(
                user.getId(),
                requestDto.getAge(),
                requestDto.getGender(),
                requestDto.getNickname(),
                requestDto.getDesc()));
        return user;
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    /**
     * username과 email을 동적으로 조건에 따라 조회
     */
    public List<UserDocument> getUserByParam(String username, String email) {
        Query query = new Query().addCriteria(Criteria.where("username").is(username));
        // 동적 조건 추가
        if (email != null) {
            query.addCriteria(Criteria.where("email").is(email));
        }
        return mongoTemplate.find(query, UserDocument.class);
    }

    /**
     * username이 특정 문자로 시작하는 사용자 조회
     */
    public List<UserDocument> getUsersByUsernamePrefix(String prefix) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").regex("^" + prefix)); // 정규 표현식: ^는 시작을 의미
        return mongoTemplate.find(query, UserDocument.class);
    }
}