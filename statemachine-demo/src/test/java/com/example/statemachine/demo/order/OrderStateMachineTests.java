package com.example.statemachine.demo.order;

import com.example.statemachine.demo.model.OrderEntity.OrderEvents;
import com.example.statemachine.demo.model.OrderEntity.OrderStates;
import org.junit.jupiter.api.Test;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.test.StateMachineTestPlanBuilder;

public class OrderStateMachineTests {

    @Test
    void stateFlowTests() throws Exception {
        StateMachine<OrderStates, OrderEvents> machine = buildMachine();
        var plan = StateMachineTestPlanBuilder.<OrderStates, OrderEvents>builder()
                .stateMachine(machine)
                // Check initial state.
                .step()
                .expectState(OrderStates.INITIAL)
                .and()
                // Check transition.
                .step()
                .sendEvent(OrderEvents.PREPARE)
                .expectState(OrderStates.PREPARED)
                .and()
                // Check transition.
                .step()
                .sendEvent(OrderEvents.PAY)
                .expectState(OrderStates.PAID)
                .and()
                // Check transition.
                .step()
                .sendEvent(OrderEvents.FULFILL)
                .expectState(OrderStates.FULFILLED)
                .and()
                .build();
        // Test execution.
        plan.test();
    }

    private StateMachine<OrderStates, OrderEvents> buildMachine() {
        return null;
    }
}
