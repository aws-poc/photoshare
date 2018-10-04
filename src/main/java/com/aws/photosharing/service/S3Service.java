package com.aws.photosharing.service;

import java.util.Date;

public interface S3Service {
    String getPresignedUrl(String userName, String id, Date expiration);
    void putObject(String userName, String id, byte[] fileContent);
    byte[] getObject(String userName, String id);
}
