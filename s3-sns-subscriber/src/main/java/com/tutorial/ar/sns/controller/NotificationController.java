package com.tutorial.ar.sns.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.config.annotation.NotificationMessage;
import org.springframework.cloud.aws.messaging.config.annotation.NotificationSubject;
import org.springframework.cloud.aws.messaging.endpoint.NotificationStatus;
import org.springframework.cloud.aws.messaging.endpoint.annotation.NotificationMessageMapping;
import org.springframework.cloud.aws.messaging.endpoint.annotation.NotificationSubscriptionMapping;
import org.springframework.cloud.aws.messaging.endpoint.annotation.NotificationUnsubscribeConfirmationMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/notification")
@Slf4j
public class NotificationController {

    @NotificationMessageMapping
    public void receiveNotification(@NotificationMessage String message, @NotificationSubject String subject) {
        log.info("Received message: {}, having subject: {}", message, subject);
    }


    @NotificationUnsubscribeConfirmationMapping
    public void unsubscribe(NotificationStatus notificationStatus) {
        notificationStatus.confirmSubscription();
        log.info("Unsubscribed from Topic");
    }


    @NotificationSubscriptionMapping
    public void subscribe(NotificationStatus notificationStatus) {
        notificationStatus.confirmSubscription();
        log.info("Subscribed to Topic");

    }

}