package com.example.mapper.request.validation;

import com.example.mapper.config.validation.AdMessageConstraint;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

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
    @Length(max = 32, groups = Ad.class)
    @NotEmpty(groups = Ad.class)
    private String contact;
    @Length(max = 64, groups = Ad.class)
    @NotEmpty(groups = Ad.class)
    private String removeGuide;
    private Boolean isAd; // 광고 여부를 설정할 수 있는 속성
}