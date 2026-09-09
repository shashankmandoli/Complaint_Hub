package com.complainthub.dao;

import com.complainthub.entity.UserProfilePhoto;

import java.util.List;

public interface UserProfilePhotoDao {
    UserProfilePhoto save(UserProfilePhoto profilePhoto);
    UserProfilePhoto findById(long id);
    UserProfilePhoto findByUserId(long userId);
    List<UserProfilePhoto> findAll();
    UserProfilePhoto update(UserProfilePhoto profilePhoto);
    void delete(long id);
}
