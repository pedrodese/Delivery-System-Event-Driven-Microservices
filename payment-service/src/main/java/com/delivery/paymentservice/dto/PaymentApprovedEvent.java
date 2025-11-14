package com.delivery.paymentservice.dto;

import com.delivery.paymentservice.enums.PaymentMethod;
import com.delivery.paymentservice.enums.PaymentStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentApprovedEvent(
        UUID id,
        UUID orderId,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        PaymentStatus status,
        String transactionId,
        LocalDateTime processedAt
) implements Serializable {}