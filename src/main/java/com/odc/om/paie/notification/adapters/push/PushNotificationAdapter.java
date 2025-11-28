package com.odc.om.paie.notification.adapters.push;

import com.odc.om.paie.notification.model.NotificationRequest;
import com.odc.om.paie.notification.model.NotificationResult;
import com.odc.om.paie.notification.ports.NotificationGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PushNotificationAdapter implements NotificationGateway {

    @Override
    public NotificationResult send(NotificationRequest request) {
        // Mock implementation for Push notification sending
        // In real implementation, integrate with FCM, APNs, etc.
        try {
            // Simulate Push sending
            log.info("Push notification sent to {} with title: {} and message: {}", request.recipient(), request.subject(), request.message());
            return new NotificationResult(true, "push-" + System.currentTimeMillis(), "Push notification sent successfully");
        } catch (Exception e) {
            log.error("Failed to send push notification to {}: {}", request.recipient(), e.getMessage());
            return new NotificationResult(false, null, "Failed to send push notification: " + e.getMessage());
        }
    }
}