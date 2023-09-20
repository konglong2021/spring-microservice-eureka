package com.bronx.orderservice.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;


@Service
public class KafkaConsumer {
    public static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);
    private final AtomicBoolean inventoryStatus = new AtomicBoolean(false);

    @KafkaListener(topics = "inventoryTopic", groupId = "inventoryGroup")
    public void inventoryConsume(boolean status) {
        LOGGER.info(String.format("Message is -> %s",status));
        inventoryStatus.set(status);
    }

    public boolean getInventoryStatus(){
        return inventoryStatus.get();
    }

}
