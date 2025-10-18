package com.delivery.orderservice.service;

import com.delivery.orderservice.dto.CreateOrderDTO;
import com.delivery.orderservice.dto.OrderResponseDTO;
import com.delivery.orderservice.enums.OrderStatus;
import com.delivery.orderservice.event.OrderEventPublisher;
import com.delivery.orderservice.exception.ResourceNotFoundException;
import com.delivery.orderservice.mapper.OrderMapper;
import com.delivery.orderservice.model.Order;
import com.delivery.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final OrderEventPublisher publisher;

    public OrderService(OrderRepository repository, OrderEventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    public OrderResponseDTO create(CreateOrderDTO dto) {
        Order order = OrderMapper.toEntity(dto);
        Order savedOrder = repository.save(order);
        publisher.publishOrderCreated(savedOrder);
        return OrderMapper.toDTO(savedOrder);
    }

    public List<OrderResponseDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(OrderMapper::toDTO)
                .toList();
    }

    public OrderResponseDTO findById(UUID id) {
        return repository.findById(id)
                .map(OrderMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found!"));
    }

    public OrderResponseDTO updateStatus(UUID id, OrderStatus status) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found!"));

        order.setStatus(status);
        Order updatedOrder = repository.save(order);
        publisher.publishOrderUpdated(updatedOrder);

        return OrderMapper.toDTO(updatedOrder);
    }

    public void cancel(UUID id) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found!"));

        order.setStatus(OrderStatus.CANCELLED);
        repository.save(order);
        publisher.publishOrderCancelled(order);
    }
}

