package com.example.mapper.request;

import com.example.mapper.validator.Ad;
import com.example.mapper.validator.AdMessageConstraint;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotEmpty;

@Getter
@Setter
@AdMessageConstraint // 클래스단위 커스텀 검증 어노테이션
public class Message {
    @Length(max = 128)
    @NotEmpty
    private String title;
    @Length(max = 1024)
    @NotEmpty
    private String body;
    @Length(max = 32, groups = Ad.class, message = "Length is not valid")
    @NotEmpty(groups = Ad.class, message = "NotEmpty is not valid")
    private String contact;
    @Length(max = 64, groups = Ad.class, message = "Length is not valid")
    @NotEmpty(groups = Ad.class, message = "NotEmpty is not valid")
    private String removeGuide;
    private boolean isAd; // 광고 여부를 설정할 수 있는 속성
}