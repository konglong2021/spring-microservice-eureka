package com.bronx.productservice.config;

import com.bronx.productservice.dto.OrderDto;
import com.bronx.productservice.dto.OrderLineItemsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQConsumer.class);

    @RabbitListener(queues = {"${rabbitmq.queue.name}"})
    public void consume(String message){
        LOGGER.info(String.format("Received message -> %s",message));
    }

    @RabbitListener(queues = {"${rabbitmq.queue.json.name}"})
    public void consumeJson(OrderDto message){
        LOGGER.info(String.format("Received message -> %s",message));
        message.getOrderLineItemsList().forEach(orderLineItemsDto -> {
            System.out.println(orderLineItemsDto.getSkuCode());
        });
    }
}
