package com.example.mongodb.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "users") // MongoDB의 컬렉션 이름
@NoArgsConstructor
public class UserDocument {
  @Id
  public String id;
  @Indexed(unique = true) // 이메일을 유니크한 인덱스로 설정
  private String username;
  private String email;

  public UserDocument(String username, String email) {
    this.username = username;
    this.email = email;
  }
}