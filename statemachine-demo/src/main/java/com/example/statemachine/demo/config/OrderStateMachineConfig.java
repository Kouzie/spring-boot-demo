package com.example.statemachine.demo.config;

import com.example.statemachine.demo.model.OrderEntity.OrderEvents;
import com.example.statemachine.demo.model.OrderEntity.OrderStates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;

@Slf4j
@Configuration
@EnableStateMachineFactory
public class OrderStateMachineConfig extends EnumStateMachineConfigurerAdapter<OrderStates, OrderEvents> {

    @Autowired
    private StateMachineRuntimePersister<OrderStates, OrderEvents, String> stateMachineRuntimePersister;

    /*
    @Override
    public void configure(StateMachineConfigurationConfigurer<OrderStates, OrderEvents> config) throws Exception {
         config
                 .withPersistence()
                 .runtimePersister(stateMachineRuntimePersister)
                 ;
        config
                .withConfiguration() // config start
                .listener(this.listener()) // 상태 머신 리스너 추가
        ;
    }*/

    @Override
    public void configure(StateMachineStateConfigurer<OrderStates, OrderEvents> states) throws Exception {
        states
                .withStates()
                .initial(OrderStates.INITIAL)
                .stateEntry(OrderStates.INITIAL, new Action<OrderStates, OrderEvents>() {
                    @Override
                    public void execute(StateContext<OrderStates, OrderEvents> context) {
                        Long orderId = Long.class.cast(context.getExtendedState().getVariables().getOrDefault("orderId", -1l));
                        log.info("entering submitted state! orderId:{}", orderId);
                    }
                })
                .state(OrderStates.CREATED)
                .state(OrderStates.PAID)
                .state(OrderStates.PREPARED)
                .end(OrderStates.FULFILLED)
                .end(OrderStates.CANCELLED)
        ;
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStates, OrderEvents> transitions) throws Exception {
        transitions
                .withExternal().source(OrderStates.CREATED).target(OrderStates.PREPARED).event(OrderEvents.PREPARE)
                .and()
                .withExternal().source(OrderStates.PREPARED).target(OrderStates.PAID).event(OrderEvents.PAY)
                .and()
                .withExternal().source(OrderStates.PAID).target(OrderStates.FULFILLED).event(OrderEvents.FULFILL)
                .and()
                .withExternal().source(OrderStates.INITIAL).target(OrderStates.CANCELLED).event(OrderEvents.CANCEL)
                .and()
                .withExternal().source(OrderStates.PREPARED).target(OrderStates.CANCELLED).event(OrderEvents.CANCEL)
                .and()
                .withExternal().source(OrderStates.PAID).target(OrderStates.REFUNDED).event(OrderEvents.REFUND)
                .and()
                .withExternal().source(OrderStates.FULFILLED).target(OrderStates.REFUNDED).event(OrderEvents.REFUND)
        ;
    }
}