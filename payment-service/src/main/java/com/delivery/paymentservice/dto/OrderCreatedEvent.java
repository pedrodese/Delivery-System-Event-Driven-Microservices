package com.delivery.paymentservice.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID id,
        String customerName,
        String address,
        String status,
        LocalDateTime createdAt
) implements Serializable {}