package com.example.amqp.normal;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Profile("amqp")
@Configuration
public class AmqpConfig {

    @Value("${rabbitmq.username}")
    private String userName;
    @Value("${rabbitmq.port}")
    private int portNumber = 5672;
    @Value("${rabbitmq.host}")
    private String hostName;
    @Value("${rabbitmq.password}")
    private String password;

    @Bean
    public ConnectionFactory connectionFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(userName);
        factory.setPassword(password);
        factory.setHost(hostName);
        factory.setPort(portNumber);
        return factory;
    }

    @Bean(name = "demoChanel")
    public Channel channel(@Autowired ConnectionFactory factory) throws IOException, TimeoutException {
        String amqExchangeName = "amq.topic";
        String amqQueueName = "demoQueue";
        Connection amqConn = factory.newConnection("demo");
        Channel channel = amqConn.createChannel();
        // 설정이 변경되면 기존 큐를 삭제하고 새로 생성해야함
        Map<String, Object> arguments = Map.of(
                "x-message-ttl", 60000,
                "x-max-length" , 10 // 최대 메시지 개수를 1000으로 제한

        );
        amqQueueName = channel.queueDeclare(amqQueueName, true, false, false, arguments).getQueue();
        // queueName: 큐의 이름
        // durable: 서버 재시작에도 살아남을 튼튼한(?) 큐로 선언할 것인지 여부
        // exclusive: 현재의 연결에 한정되는 배타적인 큐로 선언할 것인지 여부
        // autoDelete: 사용되지 않을 때 서버에 의해 자동 삭제되는 큐로 선언할 것인지 여부
        // arguments: 큐를 구성하는 다른 속성
        boolean autoAck = true;
        channel.queueBind(amqQueueName, amqExchangeName, ".computer.#");
        channel.basicConsume(amqQueueName, autoAck, new AmqpConsumer(amqQueueName, channel));
        return channel;
    }
}