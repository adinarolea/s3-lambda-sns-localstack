package com.tutorial.ar.sns.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;
import org.springframework.cloud.aws.messaging.endpoint.config.NotificationHandlerMethodArgumentResolverConfigurationUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class NotificationConfiguration implements WebMvcConfigurer {

    @Value("${aws.region}")
    private String region;

    @Value("${localstack.url}")
    private String localstackUrl;

    @Bean
    public AmazonSNS amazonSNS() {

        AmazonSNS amazonSNSAsync = AmazonSNSClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("mock", "mock")))
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(localstackUrl, region))
                .build();

        return amazonSNSAsync;
    }

    @Bean
    public NotificationMessagingTemplate notificationMessagingTemplate() {
        return new NotificationMessagingTemplate(amazonSNS());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(NotificationHandlerMethodArgumentResolverConfigurationUtils.getNotificationHandlerMethodArgumentResolver(amazonSNS()));
    }
}
