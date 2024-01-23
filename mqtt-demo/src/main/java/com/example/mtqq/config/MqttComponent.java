package com.example.mtqq.config;


import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Slf4j
@Component
public class MqttComponent {

    private String brokerAddress = "tcp://localhost";
    private String id = "guest";

    public String[] topics = {
            "/computer/part/cpu",
            "/computer/part/monitor",
            "/computer/part/keyboard",
            "/computer/part/gpu",
            "/computer/part/ram",
    };

    @PostConstruct
    public void init() throws MqttException {
        log.info("MQTT init begin.");
        MemoryPersistence persistence = new MemoryPersistence();
        MqttClient mqttClient = new MqttClient(brokerAddress, "spring boot", persistence);
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(false); //기존에 있던 세션을 지움(구독중인 메세지, 구독옵션등 모두 사라짐)
        connOpts.setConnectionTimeout(10); //10초동안 연결되지 않을경우 타임아웃
        connOpts.setKeepAliveInterval(3);
        connOpts.setAutomaticReconnect(true); //클라이언트가 서버를 찾지 못할경우 자동 재연결
        connOpts.setUserName(id);
        connOpts.setPassword(id.toCharArray());
        mqttClient.setCallback(new MqttMessageCallback());
        mqttClient.connect(connOpts);
        int[] qos = {1, 1, 1, 1, 1};
        mqttClient.subscribe(topics, qos); //모든 구독에 대해서 qos 레벨 1로 설정
        log.info("MQTT init success.");
    }
}