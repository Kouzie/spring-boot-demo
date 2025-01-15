package com.example.redis.model;

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
}
