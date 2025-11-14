package com.delivery.paymentservice.mapper;

import com.delivery.paymentservice.dto.PaymentApprovedEvent;
import com.delivery.paymentservice.dto.PaymentResponse;
import com.delivery.paymentservice.dto.ProcessPaymentRequest;
import com.delivery.paymentservice.model.Payment;

public class PaymentMapper {

    private PaymentMapper() {}

    public static Payment toEntity(ProcessPaymentRequest dto) {
        return new Payment()
                .setOrderId(dto.orderId())
                .setAmount(dto.amount())
                .setPaymentMethod(dto.paymentMethod());
    }

    public static PaymentResponse toDTO(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getTransactionId(),
                payment.getProcessedAt(),
                payment.getCreatedAt()
        );
    }

    public static PaymentApprovedEvent toApprovedEvent(Payment payment) {
        return new PaymentApprovedEvent(
                payment.getId(),
                payment.getOrderId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getTransactionId(),
                payment.getProcessedAt()
        );
    }
}