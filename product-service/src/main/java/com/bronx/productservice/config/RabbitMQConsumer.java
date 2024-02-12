package com.bronx.productservice.config;

import com.bronx.productservice.dto.OrderDto;
import com.bronx.productservice.dto.OrderLineItemsDto;
import com.bronx.productservice.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class RabbitMQConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQConsumer.class);
    private final ProductRepository productRepository;
    private final RabbitMQProducer rabbitMQProducer;

    public RabbitMQConsumer(ProductRepository productRepository, RabbitMQProducer rabbitMQProducer) {
        this.productRepository = productRepository;
        this.rabbitMQProducer = rabbitMQProducer;
    }


    @RabbitListener(queues = {"${rabbitmq.queue.name}"})
    public void consume(String message){
        LOGGER.info(String.format("Received message -> %s",message));
    }

    @RabbitListener(queues = {"${rabbitmq.queue.json.name}"})
    public void consumeJson(OrderDto message){
        LOGGER.info(String.format("Received message -> %s",message.getOrderLineItemsList()));

        if (message.getOrderLineItemsList() != null){
            List<String> skuCode = message.getOrderLineItemsList().stream().map(OrderLineItemsDto::getSkuCode).toList();
            System.out.println(skuCode);
            skuCode.stream().map(productRepository::existsProductBySkuCode).forEach(System.out::println);
             rabbitMQProducer.sendReverseProduct(true);
        }




    }
}
