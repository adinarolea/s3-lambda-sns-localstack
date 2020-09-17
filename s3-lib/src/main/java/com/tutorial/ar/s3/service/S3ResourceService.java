package com.tutorial.ar.s3.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tutorial.ar.s3.exception.BucketResourceException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@AllArgsConstructor
@Service
public class S3ResourceService implements ResourceService {

    private AmazonS3 amazonS3;

    private ObjectMapper objectMapper;

    @Override
    public void upload(String path, Object inputObject) throws BucketResourceException {
        try {
            S3Url s3Url = S3Url.fromUrl(path);
            amazonS3.putObject(s3Url.getBucket(), s3Url.getKey(), serializeObject(inputObject), null);
        } catch (SdkClientException e) {
            log.error("Unable to upload resource {} from s3 {} ", path, e.getMessage());
            throw new BucketResourceException(String.format("Unable to upload resource %s ", path), e);
        }
    }

    @Override
    public <T> T getResource(String path, TypeReference<T> type) {
        if (path == null || !path.contains("/")) {
            throw new BucketResourceException(String.format("Malformed url %s, it must have format s3://bucket/key", path));
        }
        S3Url s3Url = S3Url.fromUrl(path);
        try {
            S3Object s3object = amazonS3.getObject(s3Url.getBucket(), s3Url.getKey());
            return convertResource(s3object.getObjectContent(), type);
        } catch (SdkClientException e) {
            log.error("Unable to download resource {} from s3 {} ", path, e.getMessage());
            throw new BucketResourceException(String.format("Unable to download resource %s from s3 ", s3Url.getKey()), e);
        }
    }

    private <T> T convertResource(InputStream inputStream, TypeReference<T> type) {
        try {
            if (type.getType().getTypeName().equals(String.class.getTypeName())) {
                return (T) IOUtils.toString(inputStream);
            }
            if (type.getType().getTypeName().equals(byte[].class.getTypeName())) {
                return (T) IOUtils.toByteArray(inputStream);
            }
            return objectMapper.readValue(inputStream, type);
        } catch (IOException e) {
            throw new BucketResourceException(String.format("Unable to download resource"), e);
        }
    }


    @Getter
    private static class S3Url {

        private String bucket;

        private String key;

        private S3Url(String bucket, String key) {
            this.bucket = bucket;
            this.key = key;

        }

        public static S3Url fromUrl(String url) {
            url = url.replaceAll("//", "/")
                    .replace("s3://", "")
                    .replace("//", "");
            int indexSeparator = url.indexOf("/");
            String bucket = url.substring(0, indexSeparator);
            String key = url.substring(indexSeparator + 1);
            return new S3Url(bucket, key);
        }
    }



    private InputStream serializeObject(Object object) {
        if (object instanceof InputStream) {
            return (InputStream) object;
        }
        if (object instanceof byte[]) {
            return new ByteArrayInputStream((byte[]) object);
        }
        String serializedObject;
        if (object instanceof String) {
            serializedObject = (String) object;
        } else {
            try {
                serializedObject = objectMapper.writeValueAsString(object);
            } catch (JsonProcessingException e) {
                throw new BucketResourceException(String.format("Unable to upload resource"), e);
            }
        }
        return new ByteArrayInputStream(serializedObject.getBytes(StandardCharsets.UTF_8));
    }
}
