package com.bronx.orderservice.controller;

import com.bronx.orderservice.client.InventoryClient;
import com.bronx.orderservice.config.RabbitMQProducer;
import com.bronx.orderservice.dto.OrderDto;
import com.bronx.orderservice.dto.updateStockDto;
import com.bronx.orderservice.kafka.KafkaConsumer;
import com.bronx.orderservice.kafka.KafkaJsonProducer;
import com.bronx.orderservice.model.Order;
import com.bronx.orderservice.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/order")
@Slf4j
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;

    @Autowired
    private final RabbitMQProducer rabbitMQProducer;
    @Autowired
//    private final KafkaProducer kafkaProducer;
    private final KafkaJsonProducer<List<updateStockDto>> kafkaJsonProducer;
    @Autowired
    private final KafkaConsumer kafkaConsumer;

    @CircuitBreaker(name ="orderService", fallbackMethod = "handleErrorCase")
    @PostMapping
    public String placeOrder(@RequestBody OrderDto orderDto) throws InterruptedException {

//        Supplier<Boolean> booleanSupplier = () -> orderDto.getOrderLineItemsList().stream()
//                .allMatch(orderLineItems -> inventoryClient.checkStock(orderLineItems.getSkuCode(),orderLineItems.getQuantity()));

//       boolean allProductsInstock = circuitBreaker.run(booleanSupplier,throwable -> handleErrorCase());

        boolean allProductsInstock =orderDto.getOrderLineItemsList().stream().
                allMatch(orderLineItems -> inventoryClient.checkStock(orderLineItems.getSkuCode(),orderLineItems.getQuantity()));
//        boolean allProductsInstock = true;

        if (allProductsInstock){

            List<updateStockDto> updateStockDtoList = orderDto.getOrderLineItemsList().stream().map(
                    orderLineItems -> {
                        updateStockDto updateStockDto = new updateStockDto();
                        updateStockDto.setSkuCode(orderLineItems.getSkuCode());
                        updateStockDto.setQuantity(orderLineItems.getQuantity());
                        return updateStockDto;
                    }
            ).collect(Collectors.toList());

            boolean sent = rabbitMQProducer.sendJsonMessage(orderDto);
            if (sent){
                kafkaJsonProducer.sendMessage(updateStockDtoList,"orderTopic");
                Thread.sleep(1000);
                boolean inventoryStatus = kafkaConsumer.getInventoryStatus();
                    Order order = new Order();
                    order.setOrderLineItems(orderDto.getOrderLineItemsList());
                    order.setOrderNumber(UUID.randomUUID().toString());
                    orderRepository.save(order);

            }else {
                return "Product Service is down";
            }
            // Use feign to update inventory
//            boolean stockUpdated = updateStockDtoList.stream()
//                    .allMatch(updateStockDto -> inventoryClient.updateInventory(updateStockDto));
//            return stockUpdated ? "Order Place Successfully" : "Order failed , One products in the order is not in stock";
            return  "Order Place Successfully";
        }else {
            return "Order failed , One products in the order is not in stock";
        }

    }

    public String productServiceDown()
    {
        return "Product Service is down";
    }

    private String handleErrorCase(@RequestBody OrderDto orderDto,Throwable throwable){
        return "Inventory Service is down";
    }
}
