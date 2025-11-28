package com.odc.om.paie.notification.model;

public record NotificationResult(
        boolean success,
        String providerId,
        String info
) {}