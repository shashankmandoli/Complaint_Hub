package com.complainthub.service;

import com.complainthub.dao.UserDao;
import com.complainthub.dao.UserDaoImpl;
import com.complainthub.dao.UserProfilePhotoDao;
import com.complainthub.dao.UserProfilePhotoDaoImpl;
import com.complainthub.entity.User;
import com.complainthub.entity.UserProfilePhoto;
import com.complainthub.util.ValidationUtil;

import java.util.List;

public class UserProfilePhotoServiceImpl implements UserProfilePhotoService {

    private final UserProfilePhotoDao profilePhotoDao;
    private final UserDao userDao;

    public UserProfilePhotoServiceImpl() {
        this.profilePhotoDao = new UserProfilePhotoDaoImpl();
        this.userDao = new UserDaoImpl();
    }

    @Override
    public UserProfilePhoto createProfilePhoto(UserProfilePhoto profilePhoto) {
        if (profilePhoto == null)
            throw new IllegalArgumentException("Profile photo cannot be null");

        if (profilePhoto.getUser() == null)
            throw new IllegalArgumentException("User is required");

        long userId = profilePhoto.getUser().getId();

        ValidationUtil.validateId(userId, "User ID");

        User user = userDao.findById(userId);

        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        UserProfilePhoto existing = profilePhotoDao.findByUserId(userId);
        if (existing != null) {
            throw new IllegalArgumentException("User already has a profile photo");
        }

        validateFileMetadata(profilePhoto);
        profilePhoto.setUser(user);
        return profilePhotoDao.save(profilePhoto);
    }

    @Override
    public UserProfilePhoto getProfilePhotoById(long id) {
        ValidationUtil.validateId(id, "Profile photo ID");
        return profilePhotoDao.findById(id);
    }

    @Override
    public UserProfilePhoto getProfilePhotoByUser(long userId) {
        ValidationUtil.validateId(userId, "User ID");
        return profilePhotoDao.findByUserId(userId);
    }

    @Override
    public List<UserProfilePhoto> getAllProfilePhotos() {
        return profilePhotoDao.findAll();
    }

    @Override
    public UserProfilePhoto updateProfilePhoto(UserProfilePhoto profilePhoto) {
        if (profilePhoto == null) {
            throw new IllegalArgumentException("Profile photo cannot be null");
        }

        ValidationUtil.validateId(profilePhoto.getId(), "Profile photo ID");

        UserProfilePhoto existing = profilePhotoDao.findById(profilePhoto.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Profile photo not found");
        }

        validateFileMetadata(profilePhoto);
        profilePhoto.setUser(existing.getUser());
        return profilePhotoDao.update(profilePhoto);
    }

    @Override
    public boolean deleteProfilePhoto(long id) {
        ValidationUtil.validateId(id, "Profile photo ID");

        UserProfilePhoto existing = profilePhotoDao.findById(id);
        if (existing == null) {
            return false;
        }

        profilePhotoDao.delete(id);
        return true;
    }

    // -------- Validate Methods --------
    private void validateFileMetadata(UserProfilePhoto profilePhoto) {
        ValidationUtil.validateRequired(profilePhoto.getFileName(), "File name");
        ValidationUtil.validateMaxLength(profilePhoto.getFileName(), 255, "File name");
        ValidationUtil.validateRequired(profilePhoto.getStoredFileName(), "Stored file name");
        ValidationUtil.validateMaxLength(profilePhoto.getStoredFileName(), 255, "Stored file name");
        ValidationUtil.validateRequired(profilePhoto.getFilePath(), "File path");
        ValidationUtil.validateMaxLength(profilePhoto.getFilePath(), 1000, "File path");
        ValidationUtil.validateRequired(profilePhoto.getContentType(), "Content type");
        ValidationUtil.validateMaxLength(profilePhoto.getContentType(), 100, "Content type");

        if (profilePhoto.getFileSize() <= 0) {
            throw new IllegalArgumentException("File size must be greater than zero");
        }
    }
}