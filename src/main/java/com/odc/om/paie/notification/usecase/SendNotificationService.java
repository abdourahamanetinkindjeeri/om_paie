package com.odc.om.paie.notification.usecase;

import com.odc.om.paie.notification.model.Channel;
import com.odc.om.paie.notification.model.NotificationRequest;
import com.odc.om.paie.notification.model.NotificationResult;
import com.odc.om.paie.notification.ports.NotificationGateway;

import java.util.Map;

public class SendNotificationService implements SendNotificationUseCase {
    private final Map<Channel, NotificationGateway> gateways;

    public SendNotificationService(Map<Channel, NotificationGateway> gateways) {
        this.gateways = Map.copyOf(gateways);
    }

    @Override
    public NotificationResult execute(NotificationRequest request) {
        var gateway = gateways.get(request.channel());
        if (gateway == null) {
            return new NotificationResult(false, null, "Unsupported channel: " + request.channel());
        }
        return gateway.send(request);
    }
}