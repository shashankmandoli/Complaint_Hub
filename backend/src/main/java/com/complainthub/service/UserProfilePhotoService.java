package com.complainthub.service;

import com.complainthub.entity.UserProfilePhoto;

import java.util.List;

public interface UserProfilePhotoService {
    UserProfilePhoto createProfilePhoto(UserProfilePhoto profilePhoto);
    UserProfilePhoto getProfilePhotoById(long id);
    UserProfilePhoto getProfilePhotoByUser(long userId);
    List<UserProfilePhoto> getAllProfilePhotos();
    UserProfilePhoto updateProfilePhoto(UserProfilePhoto profilePhoto);
    boolean deleteProfilePhoto(long id);
}
