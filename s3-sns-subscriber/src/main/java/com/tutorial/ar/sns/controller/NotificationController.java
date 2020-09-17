package com.tutorial.ar.sns.controller;

import com.tutorial.ar.sns.service.NotificationService;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class NotificationController {

    private NotificationService notificationService;

    @NotificationMessageMapping
    public void receiveNotification(@NotificationMessage String s3Object, @NotificationSubject String s3Bucket) {
        log.info("Received message: {}, having subject: {}", s3Bucket, s3Object);
        notificationService.notifyChange(s3Bucket, s3Object);
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