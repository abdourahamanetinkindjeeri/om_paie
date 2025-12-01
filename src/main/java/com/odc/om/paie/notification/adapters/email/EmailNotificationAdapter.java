package com.odc.om.paie.notification.adapters.email;

import com.odc.om.paie.notification.model.NotificationRequest;
import com.odc.om.paie.notification.model.NotificationResult;
import com.odc.om.paie.notification.ports.NotificationGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationAdapter implements NotificationGateway {
    private final RestTemplate restTemplate;

    @Value("${application.resend.api-key}")
    private String apiKey;

    @Override
    public NotificationResult send(NotificationRequest request) {
        try {
            String url = "https://api.resend.com/emails";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("Content-Type", "application/json");

            Map<String, Object> body = new HashMap<>();
            body.put("from", "onboarding@resend.dev");
            body.put("to", List.of(request.recipient()));
            body.put("subject", request.subject());
            body.put("text", request.message());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            log.info("Sending email to {} with subject: {}", request.recipient(), request.subject());
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            log.info("Resend API response status: {}", response.getStatusCode());
            log.info("Resend API response body: {}", response.getBody());

            if (response.getStatusCode().is2xxSuccessful()) {
                String messageId = (String) response.getBody().get("id");
                log.info("Email sent to {} with subject: {}, messageId: {}", request.recipient(), request.subject(), messageId);
                return new NotificationResult(true, messageId, "Email sent successfully");
            } else {
                log.error("Failed to send email to {}: HTTP {}", request.recipient(), response.getStatusCode());
                return new NotificationResult(false, null, "Failed to send email: HTTP " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", request.recipient(), e.getMessage());
            return new NotificationResult(false, null, "Failed to send email: " + e.getMessage());
        }
    }
}