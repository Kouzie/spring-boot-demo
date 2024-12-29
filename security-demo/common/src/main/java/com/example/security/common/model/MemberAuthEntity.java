package com.example.security.common.model;

import com.example.security.common.config.MemberAuth;
import com.example.security.common.config.MemberRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Table(name = "tbl_members_auth")
public class MemberAuthEntity {
    @Id
    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Convert(converter = MemberAuthsConverter.class)
    private Set<MemberAuth> auths;

    @UpdateTimestamp
    private LocalDateTime updatedate;

    protected MemberAuthEntity() {
    }

    public MemberAuthEntity(MemberRole role, Set<MemberAuth> auths) {
        this.role = role;
        this.auths = auths;
    }

    @Converter
    public static class MemberAuthsConverter implements AttributeConverter<Set<MemberAuth>, String> {

        private static final String SEPARATOR = ",";

        @Override
        public String convertToDatabaseColumn(Set<MemberAuth> attribute) {
            if (attribute == null || attribute.isEmpty()) {
                return null;
            }
            return attribute.stream()
                    .map(Enum::name) // Enum 이름을 문자열로 변환
                    .collect(Collectors.joining(SEPARATOR)); // 콤마로 연결
        }

        @Override
        public Set<MemberAuth> convertToEntityAttribute(String dbData) {
            if (dbData == null || dbData.isEmpty()) {
                return Set.of();
            }
            return Arrays.stream(dbData.split(SEPARATOR))
                    .map(MemberAuth::valueOf) // 문자열을 Enum으로 변환
                    .collect(Collectors.toSet());
        }
    }

}