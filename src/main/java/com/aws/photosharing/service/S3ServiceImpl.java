package com.aws.photosharing.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

@Slf4j
@Component
public class S3ServiceImpl implements  S3Service {
    private final AmazonS3 s3;

    public S3ServiceImpl(@Autowired AmazonS3 s3) {
        this.s3 = s3;
    }

    @Override
    public String getPresignedUrl(String userName, String id, Date expiration) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(userName, id)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(expiration);
        return s3.generatePresignedUrl(generatePresignedUrlRequest).toString();
    }

    @Override
    public void putObject(String userName, String id, byte[] fileContent) {
        if (!s3.doesBucketExistV2(userName)) {
            s3.createBucket(userName);
        }
        s3.putObject(userName, id, new ByteArrayInputStream(fileContent), null);
    }

    @Override
    public byte[] getObject(String userName, String id) {
        S3Object s3Object = s3.getObject(userName, id);
        if (s3Object == null) {
            return null;
        }
        byte[] byteArray = null;
        try {
            byteArray = IOUtils.toByteArray(s3Object.getObjectContent());
        } catch (IOException e) {
            log.error("Unable to get the object from s3", e.getMessage());
            e.printStackTrace();
        }
        return byteArray;
    }
}
