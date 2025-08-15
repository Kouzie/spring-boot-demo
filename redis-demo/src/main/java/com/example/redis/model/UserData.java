package com.example.redis.model;

import com.example.redis.RedisDemoApplication;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UserData implements Serializable {
    private String nickname;
    private String username;
    private String email;
    private Integer age;
    private String desc;

    public static UserData randomUser() {
        UserData user = new UserData();
        user.setNickname(RedisDemoApplication.lorem.getName());
        user.setUsername(RedisDemoApplication.lorem.getEmail());
        user.setEmail(user.getUsername());
        user.setAge(RedisDemoApplication.random.nextInt(100));
        user.setDesc(RedisDemoApplication.lorem.getWords(10));
        return user;
    }
}
