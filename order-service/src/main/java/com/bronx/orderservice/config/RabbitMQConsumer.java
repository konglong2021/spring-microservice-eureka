package com.bronx.orderservice.config;

import com.bronx.orderservice.dto.ReverseProductDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQConsumer.class);

    @RabbitListener(queues = {"${rabbitmq.queue.name.product}"})
    public void consume(ReverseProductDto message){
        LOGGER.info(String.format("Received message -> %s",message.toString()));
    }
}
