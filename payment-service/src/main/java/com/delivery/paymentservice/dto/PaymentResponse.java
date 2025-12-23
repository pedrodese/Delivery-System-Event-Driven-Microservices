package com.delivery.paymentservice.dto;

import com.delivery.paymentservice.enums.PaymentMethod;
import com.delivery.paymentservice.enums.PaymentStatus;
import com.delivery.paymentservice.model.Payment;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaymentResponse(
        UUID id,
        UUID orderId,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        PaymentStatus status,
        String transactionId,
        LocalDateTime processedAt,
        LocalDateTime createdAt
) {
    public PaymentResponse(Payment entity) {
        this(
                entity.getId(),
                entity.getOrderId(),
                entity.getAmount(),
                entity.getPaymentMethod(),
                entity.getStatus(),
                entity.getTransactionId(),
                entity.getProcessedAt(),
                entity.getCreatedAt()
        );
    }
}