package com.delivery.orderservice.dto;

import com.delivery.orderservice.enums.OrderStatus;
import com.delivery.orderservice.model.Order;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderResponseDTO(
        UUID id,
        String customerName,
        String address,
        OrderStatus status,
        LocalDateTime createdAt
) {
    public OrderResponseDTO(Order entity) {
        this(
                entity.getId(),
                entity.getCustomerName(),
                entity.getAddress(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
