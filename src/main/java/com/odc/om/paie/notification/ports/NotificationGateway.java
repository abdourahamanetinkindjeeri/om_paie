package com.odc.om.paie.notification.ports;

import com.odc.om.paie.notification.model.NotificationRequest;
import com.odc.om.paie.notification.model.NotificationResult;

public interface NotificationGateway {
    NotificationResult send(NotificationRequest request);
}