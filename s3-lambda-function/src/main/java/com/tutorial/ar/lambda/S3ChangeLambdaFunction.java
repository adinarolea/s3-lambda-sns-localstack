package com.tutorial.ar.lambda;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.CreateTopicResult;

public class S3ChangeLambdaFunction implements RequestHandler<S3Event, String> {

    AmazonSNS amazonSNS = AmazonSNSClientBuilder
            .standard()
            .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("mock", "mock")))
            .withEndpointConfiguration(
                    new AwsClientBuilder.EndpointConfiguration("http://localhost:14566", "us-east-1"))
            .build();


    @Override
    public String handleRequest(S3Event s3Event, Context ctx) {
        // For each record.
        for (S3EventNotification.S3EventNotificationRecord record : s3Event.getRecords()) {

            String s3Key = record.getS3().getObject().getKey();
            String s3Bucket = record.getS3().getBucket().getName();

            //if the topic exists, the aws will return it in this call
            CreateTopicResult createTopicResult = amazonSNS.createTopic(s3Bucket);
            amazonSNS.publish(createTopicResult.getTopicArn(), s3Key, s3Bucket);
        }
        return "Done";
    }
}
