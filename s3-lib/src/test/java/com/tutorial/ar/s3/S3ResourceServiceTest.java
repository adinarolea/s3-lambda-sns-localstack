package com.tutorial.ar.s3;

import cloud.localstack.TestUtils;
import cloud.localstack.docker.LocalstackDockerExtension;
import cloud.localstack.docker.annotation.LocalstackDockerProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tutorial.ar.s3.service.ResourceService;
import com.tutorial.ar.s3.service.S3ResourceService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(LocalstackDockerExtension.class)
@LocalstackDockerProperties(services = {"s3"}, randomizePorts = true)
public class S3ResourceServiceTest {

    private static final String BUCKET = "test-bucket";

    private ResourceService resourceService = new S3ResourceService(TestUtils.getClientS3(), new ObjectMapper());

    @BeforeAll
    public static void init() {
        TestUtils.getClientS3().createBucket(BUCKET);
    }

    @Test
    @DisplayName("Resource upload -> can download it")
    public void whenUploadResource_thenCanDownloadIt() {
        String s3Path = BUCKET + "/myResource.json";
        String content = "{ \"test\":\"value\"}";
        resourceService.upload(s3Path, content);
        String resource = resourceService.getResource(s3Path, new TypeReference<String>() {
        });
        assertThat(resource).isEqualTo(content);
    }
}