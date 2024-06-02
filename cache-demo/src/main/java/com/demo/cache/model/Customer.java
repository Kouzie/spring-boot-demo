package com.demo.cache.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class Customer implements Serializable {
    private String id;

    private String nickName;

    private CustomerType customerType;
    private Date createTime;
}