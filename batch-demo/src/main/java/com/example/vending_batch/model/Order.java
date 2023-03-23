package com.example.vending_batch.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "[order]")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Float totalPrice;
    private Float payPrice;
    private Float orderFee;
    private Integer status;
    private String address;

    private String orderNo;
    private String description;
    private String payType;

    @CreationTimestamp
    private Date createTime;
    @UpdateTimestamp
    private Date updateTime;
//    @ManyToOne
//    @JoinColumn(name = "device_id")
//    private Device device;
//
//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;
//
//    @OneToMany(mappedBy = "order")
//    private List<OrderItem> orderItems;
}
