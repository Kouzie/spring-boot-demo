package com.example.sharding.sphere.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findAllByAccountId(Long accountId);
}
