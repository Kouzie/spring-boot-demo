package com.example.mongodb.service;

import com.example.mongodb.controller.dto.CreateUserRequestDto;
import com.example.mongodb.model.*;
import com.mongodb.*;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserDetailDocumentRepository userDetailRepository;
    private final MongoTemplate mongoTemplate;
    private final TransactionTemplate transactionTemplate;
    private final MongoClient client;

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

    // transactionTemplate
    public UserDocument updateUserBySession(String id, CreateUserRequestDto requestDto) {
        return transactionTemplate.execute((TransactionStatus action) -> {
            UserDocument user = userRepository.findById(id).orElseThrow();
            user.setEmail(requestDto.getEmail());
            user.setUsername(requestDto.getUsername());
            user = userRepository.save(user);
            if (true) throw new IllegalArgumentException(""); // 트랜잭션 확인용
            UserDetailDocument userDetail = userDetailRepository.findById(id).orElseThrow();
            userDetail.setAge(requestDto.getAge());
            userDetail.setGender(requestDto.getGender());
            userDetail.setNickname(requestDto.getNickname());
            userDetail.setDesc(requestDto.getDesc());
            userDetail = userDetailRepository.save(userDetail);
            return user;
        });
    }

    // mongoTemplate.withSession(session)
    public UserDocument updateUserBySession2(String id, CreateUserRequestDto requestDto) {
        ClientSessionOptions options = ClientSessionOptions.builder()
                .causallyConsistent(true) // 옵션 설정
                .build();
        try (ClientSession session = client.startSession(options)) {
            session.startTransaction();
            try {
                UserDocument user = userRepository.findById(id).orElseThrow();
                user.setEmail(requestDto.getEmail());
                user.setUsername(requestDto.getUsername());
                mongoTemplate.withSession(session).save(user); // User 저장
                if (true) throw new IllegalArgumentException("Rollback test");
                UserDetailDocument userDetail = userDetailRepository.findById(id).orElseThrow();
                userDetail.setAge(requestDto.getAge());
                userDetail.setGender(requestDto.getGender());
                userDetail.setNickname(requestDto.getNickname());
                userDetail.setDesc(requestDto.getDesc());
                mongoTemplate.withSession(session).save(userDetail);
                session.commitTransaction();
                return user;
            } catch (Exception e) {
                session.abortTransaction();
                throw e;
            }
        }
    }

    // MongoOperations action
    public UserDocument updateUserBySession3(String id, CreateUserRequestDto requestDto) {
        ClientSessionOptions options = ClientSessionOptions.builder()
                .causallyConsistent(true) // 옵션 설정
                .build();
        try (ClientSession session = client.startSession(options)) {
            return mongoTemplate.withSession(() -> session).execute((MongoOperations action) -> {
                session.startTransaction(); // 트랜잭션 시작
                try {
                    Query userQuery = Query.query(where("_id").is(id));
                    UserDocument user = action.findOne(userQuery, UserDocument.class);
                    user.setEmail(requestDto.getEmail());
                    user.setUsername(requestDto.getUsername());
                    action.save(user);
                    if (true) throw new IllegalArgumentException("Rollback test");
                    Query userDetailQuery = Query.query(where("_id").is(id));
                    UserDetailDocument userDetail = action.findOne(userDetailQuery, UserDetailDocument.class);
                    userDetail.setAge(requestDto.getAge());
                    userDetail.setGender(requestDto.getGender());
                    userDetail.setNickname(requestDto.getNickname());
                    userDetail.setDesc(requestDto.getDesc());
                    action.save(userDetail);
                    session.commitTransaction(); // 트랜잭션 커밋
                    return user;
                } catch (Exception e) {
                    session.abortTransaction(); // 트랜잭션 롤백
                    throw e;
                }
            });
        }
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    public UserDocument getUserByUsernameAndEmail(String username, String email) {
        return userRepository.findByUsernameAndEmail(username, email);
    }

    /**
     * username과 email을 동적으로 조건에 따라 조회
     */
    public UserDocument getUserByParam(String username, String email) {
        Query query = new Query().addCriteria(where("username").is(username));
        // 동적 조건 추가
        if (email != null) {
            query.addCriteria(where("email").is(email));
        }
        return mongoTemplate.findOne(query, UserDocument.class);
    }

    /**
     * username이 특정 문자로 시작하는 사용자 조회
     */
    public List<UserDocument> getUsersByUsernamePrefix(String prefix) {
        Query query = new Query();
        query.addCriteria(where("username").regex("^" + prefix)); // 정규 표현식: ^는 시작을 의미
        return mongoTemplate.find(query, UserDocument.class);
    }
}