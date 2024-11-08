package com.example.statemachine.demo.config;

import com.example.statemachine.demo.state.Events;
import com.example.statemachine.demo.state.States;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.action.StateDoActionPolicy;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.config.configurers.StateConfigurer;
import org.springframework.statemachine.config.model.StateMachineModel;
import org.springframework.statemachine.config.model.verifier.StateMachineModelVerifier;
import org.springframework.statemachine.ensemble.StateMachineEnsemble;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.region.RegionExecutionPolicy;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.TransitionConflictPolicy;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
//@Configuration
//@EnableStateMachineFactory
public class StateDemoConfig extends EnumStateMachineConfigurerAdapter<States, Events> {

    public StateMachineListener<States, Events> listener() {
        return new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<States, Events> from, State<States, Events> to) {
                log.info("State changed, to {}", to.getId());
            }
        };
    }

    public StateMachineEnsemble<States, Events> stateMachineEnsemble() throws Exception {
        // naturally not null but should return ensemble instance
        return null;
    }

    public StateMachineModelVerifier<States, Events> verifier() {
        return new StateMachineModelVerifier<States, Events>() {

            @Override
            public void verify(StateMachineModel<States, Events> model) {
                // throw exception indicating malformed model
            }
        };
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<States, Events> config) throws Exception {
        config
                .withConfiguration() // config start
                .machineId("myMachineId")
                .autoStartup(true) // 상태 머신의 동작 방식 autoStartup
                .listener(this.listener()) // 상태 머신 리스너 추가
                //.stateDoActionPolicy(StateDoActionPolicy.IMMEDIATE_CANCEL) // 취소요청이 들어오면 액션을 멈추고 즉시 취소
                .stateDoActionPolicy(StateDoActionPolicy.TIMEOUT_CANCEL) // 아래시간동안 액션 수행이 완료되지 않으면 자동 취소
                .stateDoActionPolicyTimeout(10, TimeUnit.SECONDS) // TIMEOUT_CANCEL 과 연계설정
                .transitionConflictPolicy(TransitionConflictPolicy.CHILD) // 전이 충돌이 발생했을 때 처리 방식, 자식 상태의 전이를 우선적으로 처리
                .regionExecutionPolicy(RegionExecutionPolicy.PARALLEL) // 여러 Region이 병렬로 실행되도록 설정
        ;
        config.withDistributed() // 상태 머신을 분산 환경에서 사용할 수 있도록 설정
                .ensemble(stateMachineEnsemble()) // 여러 인스턴스가 동일한 상태를 공유하도록 구성
        ;
        config.withVerifier() // 상태 머신을 검증하는 설정
                .enabled(true)
                .verifier(verifier()) // 상태 머신의 동작을 검증하는 특정 verifier 객체, 사용자 정의 검증 로직을 추가
        ;
    }

    /*public StateMachineModelFactory<States, Events> modelFactory() {
        return new StateMachineModelFactory<States, Events>() {
            @Override
            public StateMachineModel<States, Events> build() {
                return null;
            }

            @Override
            public StateMachineModel<States, Events> build(String machineId) {
                return null;
            }
        };
    }

    @Override
    public void configure(StateMachineModelConfigurer<States, Events> model) throws Exception {
        model.withModel()
                .factory(modelFactory());
    }*/

    @Override
    public void configure(StateMachineStateConfigurer<States, Events> states) throws Exception {
        states
                .withStates()
                .region("R0") // region R0
                .initial(States.S1)
                .states(Set.of(States.S2, States.S3))
                .end(States.SF)
                .choice(States.S1) // transitions.withChoice().source(States.S1) 필요
                .history(States.SH, StateConfigurer.History.SHALLOW)
        ;
        states
                .withStates() // region R2
                .region("R2")
                .parent(States.S2)
                .initial(States.S2I)
                .states(Set.of(States.S21, States.S22))
                .end(States.S2F)
                .fork(States.S2FK)
                .join(States.S2JN) // transitions.withFork().source().target() 필요
        ;
        states
                .withStates() // region R2-FK
                .region("R2-FK")
                .parent(States.S2FK)
                .initial(States.S2FKI)
                .states(Set.of(States.S2FK1, States.S2FK2))
        ;
        states
                .withStates() // region R3
                .region("R3")
                .parent(States.S3)
                .initial(States.S3I)
                .states(Set.of(States.S31, States.S32))
                .end(States.S3F)
                .entry(States.S3ET)
                .exit(States.S3EX)
        ;
    }

    @Bean
    public Action<States, Events> action() {
        return new Action<States, Events>() {

            @Override
            public void execute(StateContext<States, Events> context) {
                // do something
                throw new RuntimeException("MyError");
            }
        };

    }

    @Bean
    public Action<States, Events> errorAction() {
        return new Action<States, Events>() {

            @Override
            public void execute(StateContext<States, Events> context) {
                // RuntimeException("MyError") added to context
                Exception exception = context.getException();
                exception.getMessage();
            }
        };
    }


    @Bean
    public Guard<States, Events> guard() {
        return new Guard<States, Events>() {
            @Override
            public boolean evaluate(StateContext<States, Events> context) {
                return Math.random() < 0.6;
            }
        };
    }

    private Guard<States, Events> s2Guard() {
        return context -> false;
    }

    private Guard<States, Events> s3Guard() {
        return context -> true;
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<States, Events> transitions) throws Exception {
        transitions
                .withExternal()
                .source(States.S1).target(States.S2).event(Events.E1) // E1 이벤트 발생시 initial(S1) -> S2 로 전환
                .timerOnce(5000) // 5초 후 자동으로 취소
                .guard(this.guard())
                .action(this.action(), this.errorAction())
        ;
        transitions
                .withInternal()
                .source(States.S2).event(Events.E2)
        ;
        transitions
                .withLocal()
                .source(States.S21).target(States.S22).event(Events.E21)
        ;
        transitions
                .withChoice()
                .source(States.S1)
                .first(States.S2, this.s2Guard()) // 조건에 따라 S1 에서 S2 로 이동
                .then(States.S3, this.s3Guard()) // 조건에 따라 S1 에서 S3 로 이동
                .last(States.SF) // else S1 에서 SF 로 이동
        ;
        transitions
                .withFork()
                .source(States.S2FK)
                .target(States.S2FK1)
                .target(States.S2FK2)
                .and()
                .withJoin()
                .sources(List.of(States.S21, States.S22))
                .target(States.S2JN)
        ;
        transitions
                .withHistory()
                .source(States.SH)
                .target(States.S22)
        ;
    }
}