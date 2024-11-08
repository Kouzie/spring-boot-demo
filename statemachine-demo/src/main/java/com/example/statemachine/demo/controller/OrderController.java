package com.example.statemachine.demo.controller;

import com.example.statemachine.demo.adaptor.PayAdaptor;
import com.example.statemachine.demo.adaptor.exception.PayFailedException;
import com.example.statemachine.demo.controller.dto.OrderDto;
import com.example.statemachine.demo.service.OrderStateMachineService;
import com.example.statemachine.demo.service.exception.PayEventFailedException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderStateMachineService service;
    private final PayAdaptor payAdaptor;

    @PostConstruct
    private void init() throws Exception, PayFailedException, PayEventFailedException {
        OrderDto dto = service.create();
        service.prepare(dto.getOrderId());
        String paymentId = payAdaptor.pay(dto.getOrderId()); // 외부 결제
        service.pay(dto.getOrderId(), paymentId);
        service.fulfill(dto.getOrderId());
        String payCancelId = payAdaptor.payCancel(paymentId);
        service.cancel(dto.getOrderId(), payCancelId);
    }

    @PostMapping("/create")
    public OrderDto create() {
        OrderDto orderDto = service.create();
        return orderDto;
    }

    @PostMapping("/pay/{orderId}")
    public OrderDto pay(@PathVariable String orderId) throws PayEventFailedException {
        OrderDto dto = service.prepare(orderId);
        try {
            String paymentId = payAdaptor.pay(orderId);
            dto = service.pay(orderId, paymentId);
        } catch (PayFailedException e) { // 결제 실패
            log.error("pay adaptor failed, orderId:{}", orderId);
        } catch (PayEventFailedException e) { // 결제 이벤트 처리 실패, 보상트랜잭션 수행 후 예외발생
            String paymentId = e.getPaymentId();
            String payCancelId = payAdaptor.payCancel(paymentId);
            service.cancel(orderId, payCancelId);
            throw e;
        }
        return dto;
    }
}
