package com.example.sharding.sphere.demo.service;

import com.example.sharding.sphere.demo.RandomTestUtil;
import com.example.sharding.sphere.demo.dto.OrderDto;
import com.example.sharding.sphere.demo.repository.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final AccountRepository accountRepository;

    @PostConstruct
    private void init() {
        List<AccountEntity> accountEntityList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            AccountEntity account = new AccountEntity(
                    RandomTestUtil.generateRandomString(16));
            AccountEntity accountEntity = accountRepository.save(account);
            accountEntityList.add(accountEntity);
        }
        for (int i = 0; i < 20; i++) {
            int randomIndex = RandomTestUtil.generateRandomInteger(accountEntityList.size());
            AccountEntity accountEntity = accountEntityList.get(randomIndex);
            Long accountId = accountEntity.getAccountId();
            OrderEntity orderEntity = new OrderEntity(
                    RandomTestUtil.generateRandomString(10),
                    accountId);
            orderEntity = orderRepository.save(orderEntity);
            int itemCnt = RandomTestUtil.generateRandomInteger(5);
            for (int j = 0; j < itemCnt; j++) {
                OrderItemEntity orderItemEntity = new OrderItemEntity(
                        orderEntity.getOrderId(),
                        accountId,
                        RandomTestUtil.generateRandomString(10)
                );
                orderItemRepository.save(orderItemEntity);
            }
        }
    }

    @Transactional
    public OrderDto addRandomOrder(Long accountId) {
        OrderEntity entity = new OrderEntity(
                RandomTestUtil.generateRandomString(10),
                accountId);
        entity = orderRepository.save(entity);
        return toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getAllTables(Long accountId) {
        return orderRepository.findAllByAccountId(accountId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderDto getTable(Long id) {
        return orderRepository.findById(id)
                .map(this::toDto)
                .orElseThrow();
    }

    private OrderDto toDto(OrderEntity entity) {
        OrderDto dto = new OrderDto();
        dto.setOrderId(entity.getOrderId());
        dto.setTitle(entity.getTitle());
        dto.setAccountId(entity.getAccountId());
        dto.setCreateTime(entity.getCreateTime());
        return dto;
    }
}
