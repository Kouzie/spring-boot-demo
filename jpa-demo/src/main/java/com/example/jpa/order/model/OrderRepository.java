package com.example.jpa.order.model;

import com.example.jpa.order.model.id.OrderId;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends CrudRepository<Order, OrderId> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o WHERE o.orderId = :orderId")
    Optional<Order> findByIdPessimistic(OrderId orderId);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT o FROM Order o WHERE o.orderId = :orderId")
    Optional<Order> findByIdOptimistic(OrderId orderId);

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Query("SELECT o FROM Order o WHERE o.state = :state")
    List<Order> findAllByOrderStateOptimistic(OrderState state);

    @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    @Query("SELECT o FROM Order o WHERE o.state = :state")
    List<Order> findAllByOrderStatePessimistic(OrderState state);
}