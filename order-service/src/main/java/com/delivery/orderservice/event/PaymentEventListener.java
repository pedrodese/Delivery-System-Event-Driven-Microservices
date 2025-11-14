package com.delivery.orderservice.event;

import com.delivery.orderservice.constants.RabbitMQConstants;
import com.delivery.orderservice.dto.PaymentApprovedEvent;
import com.delivery.orderservice.enums.OrderStatus;
import com.delivery.orderservice.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventListener {

    private static final Logger logger = LoggerFactory.getLogger(PaymentEventListener.class);

    private final OrderService orderService;

    public PaymentEventListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @RabbitListener(queues = RabbitMQConstants.PAYMENT_APPROVED_QUEUE)
    public void handlePaymentApproved(PaymentApprovedEvent event) {
        try {
            orderService.updateStatus(event.orderId(), OrderStatus.CONFIRMED);
        } catch (Exception e) {
            logger.error("Error updating order status for order: {}", event.orderId(), e);
        }
    }

    @RabbitListener(queues = RabbitMQConstants.PAYMENT_FAILED_QUEUE)
    public void handlePaymentFailed(PaymentApprovedEvent event) {
        try {
            orderService.cancel(event.orderId());
        } catch (Exception e) {
            logger.error("Error cancelling order: {}", event.orderId(), e);
        }
    }
}