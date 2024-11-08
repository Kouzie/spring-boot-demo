package com.example.statemachine.demo.config;

import com.example.statemachine.demo.model.OrderEntity.OrderEvents;
import com.example.statemachine.demo.model.OrderEntity.OrderStates;
import com.example.statemachine.demo.model.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderStateMachineListener extends StateMachineListenerAdapter<OrderStates, OrderEvents> {
    private final OrderRepository repository;

    @Override
    public void stateChanged(State<OrderStates, OrderEvents> from, State<OrderStates, OrderEvents> to) {
        log.info("State changed from {} to {}",
                from != null ? from.getId() : "NONE",
                to != null ? to.getId() : "NONE");
    }

    @Override
    public void stateEntered(State<OrderStates, OrderEvents> state) {
        log.info("State entered, state {}", state);
    }

    @Override
    public void stateExited(State<OrderStates, OrderEvents> state) {
        log.info("State exited: {}", state.getId());
    }

    @Override
    public void eventNotAccepted(Message<OrderEvents> event) {
        OrderEvents eventPayload = event.getPayload();
        Object orderId = event.getHeaders().get("order_id");
        log.error("Event not accepted: Event = {}, OrderId = {}", eventPayload, orderId);
    }

    @Override
    public void stateMachineError(StateMachine<OrderStates, OrderEvents> stateMachine, Exception exception) {
        super.stateMachineError(stateMachine, exception);
    }
}