package com.bronx.inventoryservice.kafka;

import com.bronx.inventoryservice.dto.updateStockDto;
import com.bronx.inventoryservice.model.Inventory;
import com.bronx.inventoryservice.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.EntityNotFoundException;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class KafkaConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);
    private InventoryRepository inventoryRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final KafkaJsonProducer kafkaJsonProducer;

    public KafkaConsumer(InventoryRepository inventoryRepository, KafkaJsonProducer kafkaJsonProducer) {
        this.inventoryRepository = inventoryRepository;
        this.kafkaJsonProducer = kafkaJsonProducer;
    }

    @KafkaListener(topics = "orderTopic", groupId = "myGroup")
    public void consume(List<LinkedHashMap<String, Object>> messages) {
        LOGGER.info(String.format("Message received -> %s",messages.toString()));
        boolean allUpdatesSuccessful = true;
        try {
            for (LinkedHashMap<String, Object> message : messages) {
                updateStockDto dto = objectMapper.convertValue(message, updateStockDto.class);
                Inventory inventory = inventoryRepository.findBySkuCode(dto.getSkuCode()).orElseThrow(() -> new EntityNotFoundException("cannot find product " + dto.getSkuCode()));
                if (inventory.getStock() >= 0){
                    inventory.setStock(inventory.getStock() - dto.getQuantity());
                    inventoryRepository.save(inventory);
                }else {
                    allUpdatesSuccessful = false;
                    break;
                }

            }
            if (allUpdatesSuccessful){
                kafkaJsonProducer.sendMessage(true,"inventoryTopic");
            }


        }catch (RuntimeException exception){
            kafkaJsonProducer.sendMessage(false,"inventoryTopic");
        }
    }




}
