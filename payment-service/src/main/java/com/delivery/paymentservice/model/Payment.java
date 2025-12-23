package com.delivery.paymentservice.model;

import com.delivery.paymentservice.dto.ProcessPaymentRequest;
import com.delivery.paymentservice.enums.PaymentMethod;
import com.delivery.paymentservice.enums.PaymentStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID orderId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    private String transactionId;

    private LocalDateTime processedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Payment() {}

    public Payment(ProcessPaymentRequest dto) {
        this.orderId = dto.orderId();
        this.amount = dto.amount();
        this.paymentMethod = dto.paymentMethod();
    }

    public UUID getId() {
        return id;
    }

    public Payment setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public Payment setOrderId(UUID orderId) {
        this.orderId = orderId;
        return this;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Payment setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public Payment setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public Payment setStatus(PaymentStatus status) {
        this.status = status;
        return this;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Payment setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public Payment setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
        return this;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Payment setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.status = PaymentStatus.PROCESSING;
    }
}