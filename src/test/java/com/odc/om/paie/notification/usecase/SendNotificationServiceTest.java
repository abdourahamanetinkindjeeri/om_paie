package com.odc.om.paie.notification.usecase;

import com.odc.om.paie.notification.model.Channel;
import com.odc.om.paie.notification.model.NotificationRequest;
import com.odc.om.paie.notification.model.NotificationResult;
import com.odc.om.paie.notification.ports.NotificationGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SendNotificationServiceTest {

    private SendNotificationService service;
    private FakeEmailGateway emailGateway;
    private FakeSmsGateway smsGateway;

    @BeforeEach
    void setUp() {
        emailGateway = new FakeEmailGateway();
        smsGateway = new FakeSmsGateway();

        Map<Channel, NotificationGateway> gateways = Map.of(
                Channel.EMAIL, emailGateway,
                Channel.SMS, smsGateway
        );
        service = new SendNotificationService(gateways);
    }

    @Test
    void shouldSendEmailNotification() {
        // Given
        NotificationRequest request = new NotificationRequest(
                "test@example.com",
                "Test Subject",
                "Test Message",
                Channel.EMAIL
        );

        // When
        NotificationResult result = service.execute(request);

        // Then
        assertThat(result.success()).isTrue();
        assertThat(result.providerId()).isEqualTo("email-123");
        assertThat(result.info()).isEqualTo("Email sent");
        assertThat(emailGateway.sent).isTrue();
    }

    @Test
    void shouldSendSmsNotification() {
        // Given
        NotificationRequest request = new NotificationRequest(
                "+1234567890",
                "SMS Subject",
                "SMS Message",
                Channel.SMS
        );

        // When
        NotificationResult result = service.execute(request);

        // Then
        assertThat(result.success()).isTrue();
        assertThat(result.providerId()).isEqualTo("sms-456");
        assertThat(result.info()).isEqualTo("SMS sent");
        assertThat(smsGateway.sent).isTrue();
    }

    @Test
    void shouldReturnErrorForUnsupportedChannel() {
        // Given
        NotificationRequest request = new NotificationRequest(
                "test@example.com",
                "Subject",
                "Message",
                Channel.PUSH // Not in map
        );

        // When
        NotificationResult result = service.execute(request);

        // Then
        assertThat(result.success()).isFalse();
        assertThat(result.providerId()).isNull();
        assertThat(result.info()).isEqualTo("Unsupported channel: PUSH");
    }

    // Fake implementations for testing
    static class FakeEmailGateway implements NotificationGateway {
        boolean sent = false;

        @Override
        public NotificationResult send(NotificationRequest request) {
            sent = true;
            return new NotificationResult(true, "email-123", "Email sent");
        }
    }

    static class FakeSmsGateway implements NotificationGateway {
        boolean sent = false;

        @Override
        public NotificationResult send(NotificationRequest request) {
            sent = true;
            return new NotificationResult(true, "sms-456", "SMS sent");
        }
    }
}