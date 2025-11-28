package com.odc.om.paie.notification.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NotificationRequest(
        @NotBlank(message = "Recipient is required")
        @Email(message = "Recipient must be a valid email")
        String recipient,
        @NotBlank(message = "Subject is required")
        String subject,
        @NotBlank(message = "Message is required")
        String message,
        @NotNull(message = "Channel is required")
        Channel channel
) {}