package com.example.mtqq.component;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;

@Slf4j
@Component
@RequiredArgsConstructor
public class MqttComponent implements ApplicationListener<ApplicationReadyEvent> {
    public void init() throws Exception {
        log.info("MQTT init begin.");
        MemoryPersistence persistence = new MemoryPersistence();
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(false); //기존에 있던 세션을 지움(구독중인 메세지, 구독옵션등 모두 사라짐)
        connOpts.setConnectionTimeout(10); //10초동안 연결되지 않을경우 타임아웃
        connOpts.setKeepAliveInterval(3);
        connOpts.setAutomaticReconnect(true); //클라이언트가 서버를 찾지 못할경우 자동 재연결
        String brokerAddress = "ssl://localhost:8883";
        MqttClient mqttClient = new MqttClient(brokerAddress, "my-client-123", persistence);

        // TLS 설정
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            char[] passchar = "mypass123".toCharArray();
            keyStore.load(getClass().getClassLoader().getResourceAsStream("cert/client_123.p12"), passchar);

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, passchar);

            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null);
            trustStore.setCertificateEntry("ca",
                    CertificateFactory
                            .getInstance("X.509")
                            .generateCertificate(getClass().getClassLoader().getResourceAsStream("cert/root_ca.crt")));

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);

            SSLContext ctx = SSLContext.getInstance("TLSv1.2");
            ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            connOpts.setSocketFactory(ctx.getSocketFactory());
        } catch (Exception e) {
            log.error("Failed to set up SSL context", e);
            throw new MqttException(e);
        }

        String[] topics = {
                "/computer/part/cpu",
                "/computer/part/monitor",
                "/computer/part/keyboard",
                "/computer/part/gpu",
                "/computer/part/ram",
        };
        int[] qos = {1, 1, 1, 1, 1};
        mqttClient.setCallback(new MqttMessageCallback());
        mqttClient.connect(connOpts);
        mqttClient.subscribe(topics, qos); //모든 구독에 대해서 qos 레벨 1로 설정
        log.info("MQTT init success.");
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            init();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Slf4j
    public static class MqttMessageCallback implements MqttCallback {
        @Override
        public void connectionLost(Throwable cause) {
            log.info("connection lost.....");
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            log.info("message arrived, id:{}, payload: {}", message.getId(), new String(message.getPayload(), StandardCharsets.UTF_8));
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            log.info("delivery complete....");
        }
    }

}