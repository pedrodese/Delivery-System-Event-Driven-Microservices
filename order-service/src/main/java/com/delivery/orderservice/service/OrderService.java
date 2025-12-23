package com.delivery.orderservice.service;

import com.delivery.orderservice.dto.CreateOrderDTO;
import com.delivery.orderservice.enums.OrderStatus;
import com.delivery.orderservice.event.OrderEventPublisher;
import com.delivery.orderservice.exception.ResourceNotFoundException;
import com.delivery.orderservice.model.Order;
import com.delivery.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final OrderEventPublisher publisher;

    private static final String ORDER_NOT_FOUND_MESSAGE = "Order not found";

    public OrderService(OrderRepository repository, OrderEventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    public Order create(CreateOrderDTO dto) {
        Order order = new Order(dto);
        Order savedOrder = repository.save(order);
        publisher.publishOrderCreated(savedOrder);
        return savedOrder;
    }

    public List<Order> findAll() {
        return repository.findAll();
    }

    public Order findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND_MESSAGE));
    }

    public Order updateStatus(UUID id, OrderStatus status) {
        Order order = findOrderOrThrow(id);
        order.setStatus(status);
        Order updatedOrder = repository.save(order);
        publisher.publishOrderUpdated(updatedOrder);
        return updatedOrder;
    }

    public void cancel(UUID id) {
        Order order = findOrderOrThrow(id);
        order.setStatus(OrderStatus.CANCELLED);
        repository.save(order);
        publisher.publishOrderCancelled(order);
    }

    private Order findOrderOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND_MESSAGE));
    }
}

