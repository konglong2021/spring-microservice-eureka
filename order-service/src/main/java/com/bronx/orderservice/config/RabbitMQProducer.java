package com.bronx.orderservice.config;

import com.bronx.orderservice.dto.OrderDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    @CircuitBreaker(name = "product-service",fallbackMethod = "failSentMessage")
    public boolean sendJsonMessage(OrderDto orderDto){
        LOGGER.info(String.format("Json message sent -> %s", orderDto.toString()));
        rabbitTemplate.convertAndSend(exchange,routingJsonKey,orderDto);
        return true;
    }

    public boolean failSentMessage(OrderDto orderDto,Exception exception){
        //LOGGER.info(String.format("Message Fail -> ",data.toString()));
        return false;
    }


}
