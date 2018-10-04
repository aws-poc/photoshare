package com.aws.photosharing.repositry;

import com.aws.photosharing.entity.UserPhoto;

import java.util.List;

public interface CustomUserPhotoRepositry {
    List<UserPhoto> findByTags(String userName, String[] tags);
}
