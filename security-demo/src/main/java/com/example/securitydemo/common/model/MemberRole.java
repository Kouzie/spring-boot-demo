package com.example.securitydemo.common.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "tbl_member_role")
public class MemberRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fno;

    private String roleName;

    public MemberRole(String roleName) {
        this.roleName = roleName;
    }

    protected MemberRole() {

    }
}