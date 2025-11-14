package com.delivery.orderservice.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentApprovedEvent(
        UUID id,
        UUID orderId,
        BigDecimal amount,
        String paymentMethod,
        String status,
        String transactionId,
        LocalDateTime processedAt
) implements Serializable {}