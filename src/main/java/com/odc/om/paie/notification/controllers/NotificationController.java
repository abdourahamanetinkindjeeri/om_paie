package com.odc.om.paie.notification.controllers;

import com.odc.om.paie.notification.model.Channel;
import com.odc.om.paie.notification.model.NotificationRequest;
import com.odc.om.paie.notification.model.NotificationResult;
import com.odc.om.paie.notification.usecase.SendNotificationUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final SendNotificationUseCase sendNotificationUseCase;

    @PostMapping("/send")
    public ResponseEntity<NotificationResult> sendNotification(@Valid @RequestBody NotificationRequest request) {
        NotificationResult result = sendNotificationUseCase.execute(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/test-email")
    public ResponseEntity<NotificationResult> testEmail() {
        NotificationRequest request = new NotificationRequest(
                "dev.testghost@gmail.com",
                "bonjour jeeri",
                "Ceci est un email de test depuis le système de notifications.",
                Channel.EMAIL
        );
        NotificationResult result = sendNotificationUseCase.execute(request);
        return ResponseEntity.ok(result);
    }
}