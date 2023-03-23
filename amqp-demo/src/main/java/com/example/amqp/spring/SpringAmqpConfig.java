package com.example.amqp.spring;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
@Profile("spring-amqp")
@Configuration
public class SpringAmqpConfig {


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

    @Bean
    public TopicExchange exchangeTopic() {
        return new TopicExchange("amq.topic");
    }

    @Bean(name = "demoQueue")
    public Queue queue() {
        // durable, exclusive, autoDelete
        String queueName = "demoQueue";
        return new Queue(queueName, true, false, false);
    }

    @Bean
    public Binding binding(TopicExchange exchangeTopic, Queue queue) throws JsonProcessingException {
        log.info("biding invoked, queueName:" + queue.getName());
        log.info("queue:" + new ObjectMapper().writeValueAsString(queue.getArguments()));
        return BindingBuilder.bind(queue)
                .to(exchangeTopic)
                .with(".computer.#");
    }
}