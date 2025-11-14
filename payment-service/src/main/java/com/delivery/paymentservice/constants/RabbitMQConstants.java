package com.delivery.paymentservice.constants;

public class RabbitMQConstants {
    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String PAYMENT_EXCHANGE = "payment.exchange";

    public static final String ORDER_CREATED_QUEUE = "order.created";

    public static final String PAYMENT_APPROVED_QUEUE = "payment.approved";
    public static final String PAYMENT_FAILED_QUEUE = "payment.failed";
    public static final String PAYMENT_REFUNDED_QUEUE = "payment.refunded";

    public static final String ORDER_ROUTING_KEY_CREATED = "order.created";

    public static final String PAYMENT_ROUTING_KEY_APPROVED = "payment.approved";
    public static final String PAYMENT_ROUTING_KEY_FAILED = "payment.failed";
    public static final String PAYMENT_ROUTING_KEY_REFUNDED = "payment.refunded";
}