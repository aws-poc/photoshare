package com.aws.photosharing.service;

import com.aws.photosharing.entity.UserPhoto;
import com.aws.photosharing.model.ImageUrls;
import com.aws.photosharing.repositry.UserPhotoRepositry;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
public class PhotoShareServiceImpl implements PhotoShareService {

    private UserPhotoRepositry userPhotoRepositry;
    private S3Service s3Service;

    public PhotoShareServiceImpl(@Autowired UserPhotoRepositry userPhotoRepositry, @Autowired S3Service s3Service) {
        this.userPhotoRepositry = userPhotoRepositry;
        this.s3Service = s3Service;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public String upload(String userName, byte[] fileContent) {
        if (StringUtils.isEmpty(userName)) {
            throw new RuntimeException("Username should not be null");
        }
        if (fileContent == null) {
            throw new RuntimeException("File content should not be null");
        }
        UserPhoto userPhoto = new UserPhoto(userName);
        userPhotoRepositry.save(userPhoto);
        s3Service.putObject(userName, userPhoto.getId(), fileContent);
        return userPhoto.getId();
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED)
    public ImageUrls getByTags(String userName, String tags) {
        if (StringUtils.isEmpty(userName)) {
            throw new RuntimeException("Username should not be null");
        }
        if (StringUtils.isEmpty(tags)) {
            throw new RuntimeException("Tags should not be null");
        }

        List<UserPhoto> userPhotoList = userPhotoRepositry.findByTags(userName, tags.split(","));

        if (userPhotoList.size() == 0) {
            return null;
        }

        Date expiration = getExpirationDate();

        ImageUrls imageUrls = new ImageUrls();
        userPhotoList.forEach(up -> imageUrls.add(s3Service.getPresignedUrl(userName, up.getId(), expiration)));

        return imageUrls;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean update(String id, String tags) {
        Optional<UserPhoto> optionalUserPhoto = userPhotoRepositry.findById(id);
        if (!optionalUserPhoto.isPresent()) {
            return false;
        }
        UserPhoto userPhoto = optionalUserPhoto.get();
        userPhoto.setTags(tags);
        userPhotoRepositry.save(userPhoto);
        return true;
    }

    private Date getExpirationDate() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 60;
        expiration.setTime(expTimeMillis);
        return expiration;
    }
}
