package com.odc.om.paie.notification.model;

public record NotificationRequest(
        String recipient,
        String subject,
        String message,
        Channel channel
) {}