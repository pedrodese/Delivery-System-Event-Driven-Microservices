package com.delivery.paymentservice.event;

import com.delivery.paymentservice.constants.RabbitMQConstants;
import com.delivery.paymentservice.dto.OrderCreatedEvent;
import com.delivery.paymentservice.dto.ProcessPaymentRequest;
import com.delivery.paymentservice.enums.PaymentMethod;
import com.delivery.paymentservice.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Random;

@Component
public class OrderEventListener {

    private static final Logger logger = LoggerFactory.getLogger(OrderEventListener.class);

    private final PaymentService paymentService;
    private final Random random = new Random();

    public OrderEventListener(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @RabbitListener(queues = RabbitMQConstants.ORDER_CREATED_QUEUE)
    public void handleOrderCreated(OrderCreatedEvent event) {
        logger.info("Received order created event: {}", event);

        try {
            BigDecimal amount = generateOrderAmount();

            ProcessPaymentRequest paymentRequest = new ProcessPaymentRequest(
                    event.id(),
                    amount,
                    PaymentMethod.PIX
            );

            paymentService.processPayment(paymentRequest);
            logger.info("Payment processed successfully for order: {}", event.id());

        } catch (Exception e) {
            logger.error("Error processing payment for order: {}", event.id(), e);
        }
    }

    private BigDecimal generateOrderAmount() {
        double amount = 20.0 + (random.nextDouble() * 180.0);
        return BigDecimal.valueOf(Math.round(amount * 100.0) / 100.0);
    }
}