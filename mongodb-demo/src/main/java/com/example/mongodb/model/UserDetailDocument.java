package com.example.mongodb.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "user_details") // MongoDB의 컬렉션 이름
public class UserDetailDocument {
  @Id
  public String userId;
  private Integer age;
  private String gender;
  private String nickname;
  private String desc;

  public UserDetailDocument(String userId, Integer age, String gender, String nickname, String desc) {
    this.userId = userId;
    this.age = age;
    this.gender = gender;
    this.nickname = nickname;
    this.desc = desc;
  }
}