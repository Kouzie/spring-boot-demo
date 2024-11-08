package com.example.statemachine.demo.service;

import com.example.statemachine.demo.controller.dto.OrderDto;
import com.example.statemachine.demo.model.OrderEntity;
import com.example.statemachine.demo.model.OrderEntity.OrderEvents;
import com.example.statemachine.demo.model.OrderEntity.OrderStates;
import com.example.statemachine.demo.model.OrderRepository;
import com.example.statemachine.demo.service.exception.PayEventFailedException;
import com.example.statemachine.demo.service.exception.PersistException;
import com.example.statemachine.demo.service.exception.PrepareFailedException;
import com.example.statemachine.demo.service.exception.RestoreException;
import com.example.statemachine.demo.service.exception.CancelFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderStateMachineService {

    private static final String ORDER_ID_HEADER = "order_id";
    private static final String PAYMENT_ID_HEADER = "payment_id";
    private static final String TWO_PC_STATUS = "_2PC_STATUS";
    private static final String TWO_PC_PREPARE_STATUS = "PREPARE";
    private static final String TWO_PC_FINISHED_STATUS = "FINISHED";

    private final StateMachinePersister<OrderStates, OrderEvents, String> persister;
    private final StateMachineFactory<OrderStates, OrderEvents> factory;
    private final StateMachineListener<OrderStates, OrderEvents> listener;
    private final OrderRepository repository;
    private final StringRedisTemplate stringRedisTemplate;

    @Transactional
    public OrderDto create() {
        OrderEntity entity = repository.save(OrderEntity.create());
        return toDto(entity);
    }

    // TODO prepare 단계에선 동시 결제를 막기 위해 distribute lock 필요
    @Transactional
    public OrderDto prepare(String orderId) {
        // 상태 기계 생성 및 상태 복원
        var sm = this.restore(orderId);
        // 이벤트 수행
        Message<OrderEvents> message = MessageBuilder.withPayload(OrderEvents.PREPARE)
                .setHeader(ORDER_ID_HEADER, orderId)
                .build();
        StateMachineEventResult<OrderStates, OrderEvents> result = sm.sendEvents(Flux.just(message)).blockLast();
        if (result != null && result.getResultType() == StateMachineEventResult.ResultType.ACCEPTED) {
            OrderEntity entity = repository.findByOrderIdForUpdate(orderId).orElseThrow();
            entity.updateStates(sm.getState().getId());
            // 상태 기계 상태 저장, db 에 상태 저장
            persistState(sm, entity);
            log.info("prepare success, orderId:{}", orderId);
            return toDto(entity);
        } else {
            throw new PrepareFailedException("prepare failed, orderId:" + orderId + ", status:{}" + sm.getState().getId());
        }
    }

    /**
     * throws PersistException, EventFailedException
     */
    @Transactional
    public OrderDto pay(String orderId, String paymentId) throws PayEventFailedException {
        // 상태 기계 생성 및 상태 복원
        var sm = this.restore(orderId);
        // 이벤트 수행
        Message<OrderEvents> message = MessageBuilder.withPayload(OrderEvents.PAY)
                .setHeader(ORDER_ID_HEADER, orderId)
                .setHeader(PAYMENT_ID_HEADER, paymentId)
                .build();
        StateMachineEventResult<OrderStates, OrderEvents> result = sm.sendEvents(Flux.just(message)).blockLast();
        if (result != null && result.getResultType() == StateMachineEventResult.ResultType.ACCEPTED) {
            // 상태 기계 상태 저장, db 에 상태 저장
            OrderEntity entity = repository.findById(orderId).orElseThrow();
            entity.updateStates(sm.getState().getId());
            entity.addPaymentId(paymentId);
            persistState(sm, entity);
            log.info("pay success, orderId:{}", orderId);
            return toDto(entity);
        } else {
            throw new PayEventFailedException("pay failed, orderId:" + orderId);
        }
    }

    @Transactional
    public void fulfill(String orderId) {
        // 상태 기계 생성 및 상태 복원
        var sm = this.restore(orderId);
        // 이벤트 수행
        Message<OrderEvents> message = MessageBuilder
                .withPayload(OrderEvents.FULFILL)
                .setHeader(ORDER_ID_HEADER, orderId)
                .build();
        StateMachineEventResult<OrderStates, OrderEvents> result = sm.sendEvents(Flux.just(message)).blockLast();
        if (result != null && result.getResultType() == StateMachineEventResult.ResultType.ACCEPTED) {
            OrderEntity entity = repository.findById(orderId).orElseThrow();
            entity.updateStates(sm.getState().getId());
            // 상태 기계 상태 저장, db 에 상태 저장
            persistState(sm, entity);
            log.info("fulfill success, orderId:{}", orderId);
        }
    }

    @Transactional
    public void cancel(String orderId, String payCancelId) {
        // 상태 기계 생성 및 상태 복원
        var sm = this.restore(orderId);
        // 이벤트 수행
        Message<OrderEvents> message = MessageBuilder
                .withPayload(OrderEvents.CANCEL)
                .setHeader(ORDER_ID_HEADER, orderId)
                .build();
        StateMachineEventResult<OrderStates, OrderEvents> result = sm.sendEvents(Flux.just(message)).blockLast();
        if (result != null && result.getResultType() == StateMachineEventResult.ResultType.ACCEPTED) {
            OrderEntity entity = repository.findById(orderId).orElseThrow();
            entity.updateStates(sm.getState().getId());
            entity.setPayCancelId(payCancelId);
            // 상태 기계 상태 저장, db 에 상태 저장
            persistState(sm, entity);
            log.info("cancel success, orderId:{}", orderId);
        } else {
            throw new CancelFailedException("cancel event failed, orderId:" + orderId + ", from:" + sm.getState().getId());
        }

    }

    /**
     * 2PC=FINISHED 상태에선 DB 와 Redis 의 동기화가 완료되었다 볼 수 있지만 Lost Update 까지 막을 수 있는것은 아님
     * FINISHED 인 상태에서 동시에 2개의 스레드가 READ 하여 상태 확인 후 업데이트를 진행할 경우 둘중 하나는 Lost Update 발생
     * 만약 동시 업데이트를 막아야할 경우 분산락을 사용, 동시 업데이트가 발생할 수 없는 시나리오인지 확인 필요
     */
    public void persistState(StateMachine<OrderStates, OrderEvents> sm, OrderEntity entity) throws PersistException {
        String orderId = entity.getOrderId();
        try {
            sm.getExtendedState().getVariables().put(TWO_PC_STATUS, TWO_PC_PREPARE_STATUS);
            persister.persist(sm, orderId);
            entity = repository.save(entity);
            sm.getExtendedState().getVariables().put(TWO_PC_STATUS, TWO_PC_FINISHED_STATUS);
            persister.persist(sm, orderId);
            log.info("state machine persist success, orderId:{}, state:{}", orderId, sm.getState());
        } catch (Exception e) {
            throw new PersistException("state machine persist error, orderId:" + orderId + ", type:" + e.getClass().getSimpleName());
        }
    }

    private StateMachine<OrderStates, OrderEvents> restore(String orderId) {
        // state machine 초기화
        StateMachine<OrderStates, OrderEvents> sm = factory.getStateMachine(orderId);
        // state machine 복원
        try {
            sm = persister.restore(sm, orderId);
            String twoPcStatus = sm.getExtendedState().get(TWO_PC_STATUS, String.class);
            if (twoPcStatus != null && !twoPcStatus.equals(TWO_PC_FINISHED_STATUS)) {
                throw new RestoreException("state machine 2pc status not finished, orderId:" + orderId + ", status:" + twoPcStatus);
            }
            // 복원된 상태가 없거나 기본 상태로 설정 필요
            if (sm.getState().getId() == OrderStates.INITIAL) { // redis 에 있지 않고 초기 생성된 state machine 일 경우
                OrderEntity entity = repository.findById(orderId).orElseThrow();
                sm.stopReactively().block();
                log.debug("No persisted state found, initializing to default state");
                // DB 상태 기반으로 StateMachine 초기화
                DefaultStateMachineContext<OrderStates, OrderEvents> context = new DefaultStateMachineContext<>(
                        entity.getOrderStates(), // DB에서 가져온 상태
                        null, // 초기 이벤트는 null
                        null, // 트랜지션 정보 (필요 시 설정 가능)
                        null  // ExtendedState (필요 시 설정 가능)
                );
                sm.getStateMachineAccessor().doWithAllRegions(function -> function.resetStateMachineReactively(context).block());
            }
            // complete 상태에서 start 했을때 initial 로 변경되는 이유 확인 필요
            if (!sm.isComplete()) { // PseudoStateKind.END state
                sm.startReactively().block(); // 상태 기계 시작, 활성화 해야 이벤트 처리 가능
            }
            sm.addStateListener(listener);
            log.info("state machine restore success, current state:{}", sm.getState().getId());
        } catch (Exception e) {
            throw new RestoreException("state machine restore error, orderId:" + orderId + ", type:" + e.getClass().getSimpleName());
        }
        return sm;
    }

    public OrderDto toDto(OrderEntity entity) {
        OrderDto dto = new OrderDto();
        dto.setOrderId(entity.getOrderId());
        dto.setState(entity.getState());
        dto.setCreated(entity.getCreated());
        return dto;
    }

}
