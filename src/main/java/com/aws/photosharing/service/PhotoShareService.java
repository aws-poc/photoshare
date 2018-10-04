package com.aws.photosharing.service;

import com.aws.photosharing.model.ImageUrls;

public interface PhotoShareService {
    String upload(String userName, byte[] fileContent);
    ImageUrls getByTags(String userName, String tags);
    boolean update(String id, String tags);
}