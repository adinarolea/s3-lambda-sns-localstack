package com.tutorial.ar.s3;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class S3LibConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public AmazonS3 localstackS3(@Value("${aws.region}") String region,
                                 @Value("${localstack.url}") String localstackUrl) {
        AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
                .withPathStyleAccessEnabled(true)
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("mock", "mock")))
                .withClientConfiguration(new ClientConfiguration()
                        .withConnectionTimeout(40000))
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(localstackUrl, region))
                .build();
        return amazonS3;
    }
}
