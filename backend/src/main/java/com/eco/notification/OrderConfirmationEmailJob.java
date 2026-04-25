package com.eco.notification;

import java.util.Map;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderConfirmationEmailJob {

  @RabbitListener(queues = "order.confirmation.email")
  public void consume(Map<String, Object> payload) {
    // send email via SES/SMTP provider, retry handled by DLQ + backoff policy
    System.out.println("Sending order confirmation to: " + payload.get("email"));
  }
}
