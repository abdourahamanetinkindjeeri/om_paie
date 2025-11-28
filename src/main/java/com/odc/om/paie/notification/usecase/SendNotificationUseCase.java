package com.odc.om.paie.notification.usecase;

import com.odc.om.paie.notification.model.NotificationRequest;
import com.odc.om.paie.notification.model.NotificationResult;

public interface SendNotificationUseCase {
    NotificationResult execute(NotificationRequest request);
}