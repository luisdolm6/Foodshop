package com.foodshop.service;

import com.foodshop.model.CustomerOrder;
import com.foodshop.model.OrderItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public boolean sendOrderEmail(String toEmail, CustomerOrder order, String subject, String heading) {
        if (toEmail == null || toEmail.isBlank()) {
            return false;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail.trim());
            message.setTo(toEmail.trim());
            message.setSubject(subject);

            StringBuilder body = new StringBuilder();
            body.append(heading).append("\n\n");
            body.append("Order #").append(order.getId()).append("\n");
            body.append("Customer: ").append(order.getCustomerName()).append("\n");
            body.append("Address: ").append(order.getAddress()).append("\n\n");
            body.append("Items:\n");
            if (order.getItems() != null) {
                for (OrderItem item : order.getItems()) {
                    body.append("- ")
                        .append(item.getQuantity())
                        .append("x ")
                        .append(item.getProductName())
                        .append(" ($")
                        .append(item.getPrice())
                        .append(")\n");
                }
            }
            body.append("\nTotal: $").append(order.getTotal());

            message.setText(body.toString());
            mailSender.send(message);
            System.out.println("Email notification sent to " + toEmail);
            return true;
        } catch (Exception e) {
            System.err.println("Email failed: " + e.getMessage());
            return false;
        }
    }
}