package com.delivery.paymentservice.event;

import com.delivery.paymentservice.constants.RabbitMQConstants;
import com.delivery.paymentservice.dto.PaymentApprovedEvent;
import com.delivery.paymentservice.mapper.PaymentMapper;
import com.delivery.paymentservice.model.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(PaymentEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public PaymentEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishPaymentApproved(Payment payment) {
        logger.info("Publishing payment approved event for order: {}", payment.getOrderId());
        PaymentApprovedEvent event = PaymentMapper.toApprovedEvent(payment);
        rabbitTemplate.convertAndSend(
                RabbitMQConstants.PAYMENT_EXCHANGE,
                RabbitMQConstants.PAYMENT_ROUTING_KEY_APPROVED,
                event
        );
        logger.info("Payment approved event published successfully: {}", event);
    }

    public void publishPaymentFailed(Payment payment) {
        logger.info("Publishing payment failed event for order: {}", payment.getOrderId());
        PaymentApprovedEvent event = PaymentMapper.toApprovedEvent(payment);
        rabbitTemplate.convertAndSend(
                RabbitMQConstants.PAYMENT_EXCHANGE,
                RabbitMQConstants.PAYMENT_ROUTING_KEY_FAILED,
                event
        );
        logger.info("Payment failed event published successfully");
    }

    public void publishPaymentRefunded(Payment payment) {
        logger.info("Publishing payment refunded event for order: {}", payment.getOrderId());
        PaymentApprovedEvent event = PaymentMapper.toApprovedEvent(payment);
        rabbitTemplate.convertAndSend(
                RabbitMQConstants.PAYMENT_EXCHANGE,
                RabbitMQConstants.PAYMENT_ROUTING_KEY_REFUNDED,
                event
        );
        logger.info("Payment refunded event published successfully");
    }
}