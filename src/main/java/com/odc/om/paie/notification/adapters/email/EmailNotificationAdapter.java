//package com.odc.om.paie.notification.adapters.email;
//
//import com.odc.om.paie.notification.model.NotificationRequest;
//import com.odc.om.paie.notification.model.NotificationResult;
//import com.odc.om.paie.notification.ports.NotificationGateway;
//
//public class EmailNotificationAdapter implements NotificationGateway {
//    private final MailClient client; // e.g., JavaMail, SendGrid, etc.
//
//    public EmailNotificationAdapter(MailClient client) {
//        this.client = client;
//    }
//
//    @Override
//    public NotificationResult send(NotificationRequest request) {
//        var id = client.send(request.recipient(), request.subject(), request.message());
//        return new NotificationResult(true, id, "Email sent");
//    }
//}