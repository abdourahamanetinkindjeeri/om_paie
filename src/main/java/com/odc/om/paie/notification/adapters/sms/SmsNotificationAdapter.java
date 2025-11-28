package com.odc.om.paie.notification.adapters.sms;

import com.odc.om.paie.notification.model.NotificationRequest;
import com.odc.om.paie.notification.model.NotificationResult;
import com.odc.om.paie.notification.ports.NotificationGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SmsNotificationAdapter implements NotificationGateway {

    @Override
    public NotificationResult send(NotificationRequest request) {
        // Mock implementation for SMS sending
        // In real implementation, integrate with SMS provider like Twilio, AWS SNS, etc.
        try {
            // Simulate SMS sending
            log.info("SMS sent to {} with message: {}", request.recipient(), request.message());
            return new NotificationResult(true, "sms-" + System.currentTimeMillis(), "SMS sent successfully");
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", request.recipient(), e.getMessage());
            return new NotificationResult(false, null, "Failed to send SMS: " + e.getMessage());
        }
    }
}