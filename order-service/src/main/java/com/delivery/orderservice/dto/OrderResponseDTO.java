package com.delivery.orderservice.dto;

import com.delivery.orderservice.enums.OrderStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrderResponseDTO(
        UUID id,
        String customerName,
        String address,
        OrderStatus status,
        LocalDateTime createdAt
) {}
