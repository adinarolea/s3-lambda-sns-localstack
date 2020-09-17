package com.tutorial.ar.s3.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tutorial.ar.s3.exception.BucketResourceException;

public interface ResourceService {

    <T> T getResource(String path, TypeReference<T> type);

    void upload(String path, Object inputObject) throws BucketResourceException;
}
