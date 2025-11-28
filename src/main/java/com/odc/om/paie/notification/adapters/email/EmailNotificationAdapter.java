package com.odc.om.paie.notification.adapters.email;

import com.odc.om.paie.notification.model.NotificationRequest;
import com.odc.om.paie.notification.model.NotificationResult;
import com.odc.om.paie.notification.ports.NotificationGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationAdapter implements NotificationGateway {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:}")
    private String fromAddress;

    @Override
    public NotificationResult send(NotificationRequest request) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            if (fromAddress != null && !fromAddress.isEmpty()) {
                message.setFrom(fromAddress);
            }
            message.setTo(request.recipient());
            message.setSubject(request.subject());
            message.setText(request.message());

            mailSender.send(message);

            log.info("Email sent to {} with subject: {}", request.recipient(), request.subject());
            return new NotificationResult(true, "email-" + System.currentTimeMillis(), "Email sent successfully");
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", request.recipient(), e.getMessage());
            return new NotificationResult(false, null, "Failed to send email: " + e.getMessage());
        }
    }
}