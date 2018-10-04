package com.aws.photosharing.repositry;

import com.aws.photosharing.entity.UserPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPhotoRepositry extends JpaRepository<UserPhoto, String>, CustomUserPhotoRepositry {
}
