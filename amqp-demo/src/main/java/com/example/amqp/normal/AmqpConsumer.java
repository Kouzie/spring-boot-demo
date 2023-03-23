package com.example.amqp.normal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;

import java.util.Map;

@Slf4j
public class AmqpConsumer extends DefaultConsumer {
    private final String consumerName;

    public AmqpConsumer(String consumerName, Channel channel) {
        super(channel);
        this.consumerName = consumerName;
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws JsonProcessingException {
        String routingKey = envelope.getRoutingKey();
        long deliveryTag = envelope.getDeliveryTag();
        String contentType = properties.getContentType();
        // (process the message components here ...)
        log.info("{} - body:{}, route:{}, content-type:{}, delivery-tag:{}, consumer-tag:{}", consumerName, new String(body), routingKey, contentType, deliveryTag, consumerTag);
        // log.info("headers:" + convertWithIteration(properties.getHeaders()));
        if (routingKey.startsWith("connection."))
            convertWithIteration(properties.getHeaders());
//            log.info("headers:" + );
        // 메세지 수신여부를 broker 에게 알림
        //this.getChannel().basicAck(deliveryTag, false);
    }

    public String convertWithIteration(Map<String, ?> map) {
        StringBuilder mapAsString = new StringBuilder("{");
//        String connectionId = map.get("name").toString();
        for (String key : map.keySet()) {
            if (key.equals("variable_map"))
                log.info("variable_map: {}, name:{}", map.get(key).toString(), map.get("name"));
            mapAsString.append(key + "=" + map.get(key) + ", ");
        }
        mapAsString.delete(mapAsString.length() - 2, mapAsString.length()).append("}");
        return mapAsString.toString();
    }
}
