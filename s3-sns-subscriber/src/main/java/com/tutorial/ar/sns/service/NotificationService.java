package com.tutorial.ar.sns.service;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.net.URL;

@Service
@Slf4j
public class NotificationService {

    @Value("${subscription.url}")
    private URL subscriptionUrl;

    @Value("${subscription.topic}")
    private String topic;

    @Autowired
    private AmazonSNS amazonSNS;

    @EventListener(ContextRefreshedEvent.class)
    public void subscribeToTopic() {

        log.info("Subscribing to topic {} with url {} ", topic, subscriptionUrl);
        CreateTopicResult createTopicResult = amazonSNS.createTopic(topic);
        SubscribeRequest subscribeRequest = new SubscribeRequest()
                .withTopicArn(createTopicResult.getTopicArn())
                .withEndpoint(subscriptionUrl.toString())
                .withProtocol(subscriptionUrl.getProtocol());

        amazonSNS.subscribe(subscribeRequest);

    }
}
