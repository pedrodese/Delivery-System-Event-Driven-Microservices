package com.delivery.paymentservice.service;

import com.delivery.paymentservice.dto.PaymentResponse;
import com.delivery.paymentservice.dto.ProcessPaymentRequest;
import com.delivery.paymentservice.enums.PaymentStatus;
import com.delivery.paymentservice.event.PaymentEventPublisher;
import com.delivery.paymentservice.exception.PaymentException;
import com.delivery.paymentservice.exception.ResourceNotFoundException;
import com.delivery.paymentservice.mapper.PaymentMapper;
import com.delivery.paymentservice.model.Payment;
import com.delivery.paymentservice.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository repository;
    private final PaymentEventPublisher eventPublisher;

    public PaymentService(PaymentRepository repository, PaymentEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public PaymentResponse processPayment(ProcessPaymentRequest request) {
        logger.info("Processing payment for order: {}", request.orderId());

        if (repository.existsByOrderId(request.orderId())) {
            throw new PaymentException("Payment already exists for this order");
        }

        Payment payment = PaymentMapper.toEntity(request);
        payment.setStatus(PaymentStatus.PROCESSING);
        Payment savedPayment = repository.save(payment);

        try {
            processPaymentByMethod(savedPayment);
            savedPayment.setStatus(PaymentStatus.APPROVED);
            savedPayment.setProcessedAt(LocalDateTime.now());
            savedPayment.setTransactionId(generateTransactionId());

            Payment approvedPayment = repository.save(savedPayment);
            logger.info("Payment approved for order: {} with transaction: {}",
                    request.orderId(), approvedPayment.getTransactionId());

            eventPublisher.publishPaymentApproved(approvedPayment);

            return PaymentMapper.toDTO(approvedPayment);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Payment processing interrupted for order: {}", request.orderId(), e);
            savedPayment.setStatus(PaymentStatus.FAILED);
            savedPayment.setProcessedAt(LocalDateTime.now());
            Payment failedPayment = repository.save(savedPayment);

            eventPublisher.publishPaymentFailed(failedPayment);

            throw new PaymentException("Payment processing was interrupted");
        } catch (Exception e) {
            logger.error("Payment failed for order: {}", request.orderId(), e);
            savedPayment.setStatus(PaymentStatus.FAILED);
            savedPayment.setProcessedAt(LocalDateTime.now());
            Payment failedPayment = repository.save(savedPayment);

            eventPublisher.publishPaymentFailed(failedPayment);

            throw new PaymentException("Payment processing failed: " + e.getMessage());
        }
    }

    private void processPaymentByMethod(Payment payment) throws InterruptedException {
        switch (payment.getPaymentMethod()) {
            case PIX -> {
                logger.info("Processing PIX payment - instant approval");
                Thread.sleep(500);
            }
            case CREDIT_CARD -> {
                logger.info("Processing CREDIT_CARD payment - simulating 3s delay");
                Thread.sleep(3000);
            }
            case DEBIT_CARD -> {
                logger.info("Processing DEBIT_CARD payment - simulating 2s delay");
                Thread.sleep(2000);
            }
            case CASH -> {
                logger.info("Processing CASH payment - pre-approved");
                Thread.sleep(500);
            }
        }
    }

    public PaymentResponse getByOrderId(UUID orderId) {
        Payment payment = repository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order: " + orderId));
        return PaymentMapper.toDTO(payment);
    }

    public List<PaymentResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(PaymentMapper::toDTO)
                .toList();
    }

    @Transactional
    public PaymentResponse refund(UUID id) {
        Payment payment = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.APPROVED) {
            throw new PaymentException("Only approved payments can be refunded");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setProcessedAt(LocalDateTime.now());
        Payment refundedPayment = repository.save(payment);

        logger.info("Payment refunded: {} for order: {}", id, payment.getOrderId());
        eventPublisher.publishPaymentRefunded(refundedPayment);

        return PaymentMapper.toDTO(refundedPayment);
    }

    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}