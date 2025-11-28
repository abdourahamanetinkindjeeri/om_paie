package com.odc.om.paie.notification.config;

import com.odc.om.paie.notification.adapters.email.EmailNotificationAdapter;
import com.odc.om.paie.notification.adapters.push.PushNotificationAdapter;
import com.odc.om.paie.notification.adapters.sms.SmsNotificationAdapter;
import com.odc.om.paie.notification.model.Channel;
import com.odc.om.paie.notification.ports.NotificationGateway;
import com.odc.om.paie.notification.usecase.SendNotificationService;
import com.odc.om.paie.notification.usecase.SendNotificationUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class NotificationConfig {

    @Bean
    public SendNotificationUseCase sendNotificationUseCase(
            EmailNotificationAdapter emailAdapter,
            SmsNotificationAdapter smsAdapter,
            PushNotificationAdapter pushAdapter
    ) {
        Map<Channel, NotificationGateway> gateways = Map.of(
                Channel.EMAIL, emailAdapter,
                Channel.SMS, smsAdapter,
                Channel.PUSH, pushAdapter
        );
        return new SendNotificationService(gateways);
    }
}