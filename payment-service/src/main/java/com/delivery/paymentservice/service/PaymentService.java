package com.delivery.paymentservice.service;

import com.delivery.paymentservice.dto.ProcessPaymentRequest;
import com.delivery.paymentservice.enums.PaymentStatus;
import com.delivery.paymentservice.event.PaymentEventPublisher;
import com.delivery.paymentservice.exception.PaymentException;
import com.delivery.paymentservice.exception.ResourceNotFoundException;
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
    public Payment processPayment(ProcessPaymentRequest request) {
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

    public Payment getByOrderId(UUID orderId) {
        return findPaymentByOrderIdOrThrow(orderId);
    }

    public List<Payment> findAll() {
        return repository.findAll()
                .stream()
                .toList();
    }

    @Transactional
    public Payment refund(UUID id) {
        Payment payment = findPaymentByIdOrThrow(id);
        validatePaymentCanBeRefunded(payment);
        return executeRefund(payment);
    }

    private void validatePaymentDoesNotExist(UUID orderId) {
        if (repository.existsByOrderId(orderId)) {
            throw new PaymentException("Payment already exists for this order");
        }
    }

    private void validatePaymentCanBeRefunded(Payment payment) {
        if (payment.getStatus() != PaymentStatus.APPROVED) {
            throw new PaymentException("Only approved payments can be refunded");
        }
    }

    private Payment createPendingPayment(ProcessPaymentRequest request) {
        Payment payment = new Payment(request);
        return repository.save(payment);
    }

    private Payment approvePayment(Payment payment) throws InterruptedException {
        processPaymentByMethod(payment);
        Payment approvedPayment = updatePaymentAsApproved(payment);
        eventPublisher.publishPaymentApproved(approvedPayment);
        return approvedPayment;
    }

    private Payment updatePaymentAsApproved(Payment payment) {
        payment.setStatus(PaymentStatus.APPROVED);
        payment.setProcessedAt(LocalDateTime.now());
        payment.setTransactionId(generateTransactionId());
        return repository.save(payment);
    }

    private Payment executeRefund(Payment payment) {
        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setProcessedAt(LocalDateTime.now());
        Payment refundedPayment = repository.save(payment);
        eventPublisher.publishPaymentRefunded(refundedPayment);
        return refundedPayment;
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

    private Payment handleInterruptedException(Payment payment, InterruptedException e) {
        Thread.currentThread().interrupt();
        logger.error("Payment processing interrupted for order: {}", payment.getOrderId(), e);

        Payment failedPayment = markPaymentAsFailed(payment);
        eventPublisher.publishPaymentFailed(failedPayment);

        throw new PaymentException("Payment processing was interrupted");
    }

    private Payment handlePaymentFailure(Payment payment, Exception e) {
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
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
    }

    private Payment findPaymentByOrderIdOrThrow(UUID orderId) {
        return repository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
    }
}