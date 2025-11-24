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
    private static final String TRANSACTION_PREFIX = "TXN-";
    private static final int TRANSACTION_ID_LENGTH = 8;

    private final PaymentRepository repository;
    private final PaymentEventPublisher eventPublisher;

    public PaymentService(PaymentRepository repository, PaymentEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public PaymentResponse processPayment(ProcessPaymentRequest request) {
        logger.info("Processing payment for order: {}", request.orderId());
        validatePaymentDoesNotExist(request.orderId());
        Payment payment = createPendingPayment(request);
        try {
            return approvePayment(payment);
        } catch (InterruptedException e) {
            return handleInterruptedException(payment, e);
        } catch (Exception e) {
            return handlePaymentFailure(payment, e);
        }
    }

    public PaymentResponse getByOrderId(UUID orderId) {
        Payment payment = findPaymentByOrderIdOrThrow(orderId);
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
        Payment payment = findPaymentByIdOrThrow(id);

        validatePaymentCanBeRefunded(payment);

        return executeRefund(payment);
    }

    private void validatePaymentDoesNotExist(UUID orderId) {
        if (repository.existsByOrderId(orderId)) {
            logger.warn("Payment already exists for order: {}", orderId);
            throw new PaymentException("Payment already exists for this order");
        }
    }

    private void validatePaymentCanBeRefunded(Payment payment) {
        if (payment.getStatus() != PaymentStatus.APPROVED) {
            logger.warn("Attempted to refund non-approved payment: {} with status: {}",
                    payment.getId(), payment.getStatus());
            throw new PaymentException("Only approved payments can be refunded");
        }
    }

    private Payment createPendingPayment(ProcessPaymentRequest request) {
        Payment payment = PaymentMapper.toEntity(request);
        payment.setStatus(PaymentStatus.PROCESSING);
        return repository.save(payment);
    }

    private PaymentResponse approvePayment(Payment payment) throws InterruptedException {
        processPaymentByMethod(payment);
        Payment approvedPayment = updatePaymentAsApproved(payment);

        logger.info("Payment approved for order: {} with transaction: {}",
                approvedPayment.getOrderId(), approvedPayment.getTransactionId());

        eventPublisher.publishPaymentApproved(approvedPayment);

        return PaymentMapper.toDTO(approvedPayment);
    }

    private Payment updatePaymentAsApproved(Payment payment) {
        payment.setStatus(PaymentStatus.APPROVED);
        payment.setProcessedAt(LocalDateTime.now());
        payment.setTransactionId(generateTransactionId());
        return repository.save(payment);
    }

    private PaymentResponse executeRefund(Payment payment) {
        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setProcessedAt(LocalDateTime.now());

        Payment refundedPayment = repository.save(payment);

        logger.info("Payment refunded: {} for order: {}",
                refundedPayment.getId(), refundedPayment.getOrderId());

        eventPublisher.publishPaymentRefunded(refundedPayment);

        return PaymentMapper.toDTO(refundedPayment);
    }

    private void processPaymentByMethod(Payment payment) throws InterruptedException {
        switch (payment.getPaymentMethod()) {
            case PIX -> processPixPayment();
            case CREDIT_CARD -> processCreditCardPayment();
            case DEBIT_CARD -> processDebitCardPayment();
            case CASH -> processCashPayment();
        }
    }

    private void processPixPayment() throws InterruptedException {
        logger.info("Processing PIX payment - instant approval");
        Thread.sleep(500);
    }

    private void processCreditCardPayment() throws InterruptedException {
        logger.info("Processing CREDIT_CARD payment - simulating 3s delay");
        Thread.sleep(3000);
    }

    private void processDebitCardPayment() throws InterruptedException {
        logger.info("Processing DEBIT_CARD payment - simulating 2s delay");
        Thread.sleep(2000);
    }

    private void processCashPayment() throws InterruptedException {
        logger.info("Processing CASH payment - pre-approved");
        Thread.sleep(500);
    }

    private PaymentResponse handleInterruptedException(Payment payment, InterruptedException e) {
        Thread.currentThread().interrupt();
        logger.error("Payment processing interrupted for order: {}", payment.getOrderId(), e);

        Payment failedPayment = markPaymentAsFailed(payment);
        eventPublisher.publishPaymentFailed(failedPayment);

        throw new PaymentException("Payment processing was interrupted");
    }

    private PaymentResponse handlePaymentFailure(Payment payment, Exception e) {
        logger.error("Payment failed for order: {}", payment.getOrderId(), e);

        Payment failedPayment = markPaymentAsFailed(payment);
        eventPublisher.publishPaymentFailed(failedPayment);

        throw new PaymentException("Payment processing failed: " + e.getMessage());
    }

    private Payment markPaymentAsFailed(Payment payment) {
        payment.setStatus(PaymentStatus.FAILED);
        payment.setProcessedAt(LocalDateTime.now());
        return repository.save(payment);
    }

    private String generateTransactionId() {
        return TRANSACTION_PREFIX +
                UUID.randomUUID()
                        .toString()
                        .substring(0, TRANSACTION_ID_LENGTH)
                        .toUpperCase();
    }

    private Payment findPaymentByIdOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Payment not found with id: {}", id);
                    return new ResourceNotFoundException("Payment not found");
                });
    }

    private Payment findPaymentByOrderIdOrThrow(UUID orderId) {
        return repository.findByOrderId(orderId)
                .orElseThrow(() -> {
                    logger.warn("Payment not found for order: {}", orderId);
                    return new ResourceNotFoundException("Payment not found for order: " + orderId);
                });
    }
}