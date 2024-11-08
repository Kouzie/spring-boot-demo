package com.example.statemachine.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String orderId;
    private String state;
    private String paymentId;
    @Setter
    private String payCancelId;
    private LocalDateTime created;

    protected OrderEntity() {
    }

    public void updateStates(OrderStates orderStates) {
        this.state = orderStates.name();
    }

    @JsonIgnore
    public OrderStates getOrderStates() {
        return OrderStates.valueOf(state);
    }

    public static OrderEntity create() {
        OrderEntity entity = new OrderEntity();
        entity.state = OrderStates.CREATED.name();
        entity.created = LocalDateTime.now();
        return entity;
    }

    public void addPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public enum OrderStates {
        INITIAL, // StateMachine 초기상태
        CREATED, // Order 생성상태
        PREPARED, // 결제 직전
        PAID, // 결제 완료
        FULFILLED, // 구매 확정
        CANCELLED, // 취소
        REFUNDED, // 환불
    }

    public enum OrderEvents {
        PREPARE, // 초기상태에서 결제직전, 중복결제 요청 진입을 막기 위한
        FULFILL,
        PAY,
        CANCEL,
        REFUND,
    }

}

