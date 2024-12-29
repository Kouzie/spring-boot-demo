package com.example.security.common.model;

import com.example.security.common.config.MemberRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "tbl_members")
public class MemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;
    private String uname;
    private String upw;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @CreationTimestamp
    private LocalDateTime regdate;
    @UpdateTimestamp
    private LocalDateTime updatedate;

    public MemberEntity(String uname, String upw, MemberRole role) {
        this.uname = uname;
        this.upw = upw;
        this.role = role;
    }

    protected MemberEntity() {

    }
}