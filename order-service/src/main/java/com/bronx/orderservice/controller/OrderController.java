package com.bronx.orderservice.controller;

import com.bronx.orderservice.client.InventoryClient;
import com.bronx.orderservice.config.RabbitMQProducer;
import com.bronx.orderservice.dto.OrderDto;
import com.bronx.orderservice.dto.updateStockDto;
import com.bronx.orderservice.model.Order;
import com.bronx.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreaker;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/order")
@Slf4j
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final Resilience4JCircuitBreakerFactory circuitBreakerFactory;

    @Autowired
    private final RabbitMQProducer rabbitMQProducer;


    @PostMapping
    public String placeOrder(@RequestBody OrderDto orderDto){

        //Circuit Breaker here
        Resilience4JCircuitBreaker circuitBreaker = circuitBreakerFactory.create("inventory");
        Supplier<Boolean> booleanSupplier = () -> orderDto.getOrderLineItemsList().stream()
                .allMatch(orderLineItems -> inventoryClient.checkStock(orderLineItems.getSkuCode(),orderLineItems.getQuantity()));

       boolean allProductsInstock = circuitBreaker.run(booleanSupplier,throwable -> handleErrorCase());


        if (allProductsInstock){
            Order order = new Order();
            order.setOrderLineItems(orderDto.getOrderLineItemsList());
            order.setOrderNumber(UUID.randomUUID().toString());
            orderRepository.save(order);

            List<updateStockDto> updateStockDtoList = orderDto.getOrderLineItemsList().stream().map(
                    orderLineItems -> {
                        updateStockDto updateStockDto = new updateStockDto();
                        updateStockDto.setSkuCode(orderLineItems.getSkuCode());
                        updateStockDto.setQuantity(orderLineItems.getQuantity());
                        return updateStockDto;
                    }
            ).collect(Collectors.toList());

            boolean stockUpdated = updateStockDtoList.stream()
                    .allMatch(updateStockDto -> inventoryClient.updateInventory(updateStockDto));

            rabbitMQProducer.sendMessage("Order Place Successfully");
            rabbitMQProducer.sendJsonMessage(orderDto);


            return stockUpdated ? "Order Place Successfully" : "Order failed , One products in the order is not in stock";
        }else {
            return "Order failed , One products in the order is not in stock";
        }

    }

    private Boolean handleErrorCase() {
        return false;
    }
}
