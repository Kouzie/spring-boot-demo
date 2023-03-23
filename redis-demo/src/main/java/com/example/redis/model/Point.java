package com.example.redis.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@ToString
@RedisHash("point")
public class Point {
    @Id
    private String id;
    private Long amount;
    private LocalDateTime refreshTime;

    public void refresh(Long amount, LocalDateTime refreshTime) {
        if (refreshTime.isAfter(this.refreshTime)) {
            this.amount = amount;
            this.refreshTime = refreshTime;
        }
    }

    public void update(Point point) {
        this.amount = point.amount;
        this.refreshTime = point.refreshTime;
    }
}
