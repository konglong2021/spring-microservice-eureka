package com.bronx.productservice.config;

import com.bronx.productservice.dto.ReverseProductDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class RabbitMQProducer {
    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.json.key}")
    private String routingJsonKey;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQProducer.class);
    private final RabbitTemplate rabbitTemplate;

    public RabbitMQProducer(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate=rabbitTemplate;
    }

    public void sendMessage(String message){
        LOGGER.info(String.format("Message sent -> %s",message));
        rabbitTemplate.convertAndSend(exchange,routingKey,message);
    }

    public void sendReverseProduct(Boolean existed){

        ReverseProductDto reverseProductDto = new ReverseProductDto();
        reverseProductDto.setExisted(existed);
        rabbitTemplate.convertAndSend(exchange,routingJsonKey,reverseProductDto);
    }

}
