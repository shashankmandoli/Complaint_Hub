package com.complainthub.service;

import com.complainthub.dao.UserDao;
import com.complainthub.dao.UserDaoImpl;
import com.complainthub.dao.UserProfilePhotoDao;
import com.complainthub.dao.UserProfilePhotoDaoImpl;
import com.complainthub.entity.User;
import com.complainthub.entity.UserProfilePhoto;
import com.complainthub.util.FileValidationUtil;
import com.complainthub.util.ValidationUtil;

import java.util.List;

public class UserProfilePhotoServiceImpl implements UserProfilePhotoService {

    private final UserProfilePhotoDao profilePhotoDao;
    private final UserDao userDao;
    private final FileStorageService fileStorageService;

    public UserProfilePhotoServiceImpl() {
        this.profilePhotoDao = new UserProfilePhotoDaoImpl();
        this.userDao = new UserDaoImpl();
        this.fileStorageService = new LocalFileStorageService();
    }

    @Override
    public UserProfilePhoto createProfilePhoto(UserProfilePhoto profilePhoto) {
        if (profilePhoto == null) {
            throw new IllegalArgumentException("Profile photo cannot be null");
        }

        if (profilePhoto.getUser() == null) {
            throw new IllegalArgumentException("User is required");
        }

        long userId = profilePhoto.getUser().getId();

        ValidationUtil.validateId(userId, "User ID");

        User user = userDao.findById(userId);

        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        UserProfilePhoto existing =
                profilePhotoDao.findByUserId(userId);

        if (existing != null) {
            throw new IllegalArgumentException(
                    "User already has a profile photo"
            );
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
    public UserProfilePhoto updateProfilePhoto(
            UserProfilePhoto profilePhoto
    ) {

        if (profilePhoto == null) {
            throw new IllegalArgumentException(
                    "Profile photo cannot be null"
            );
        }

        ValidationUtil.validateId(
                profilePhoto.getId(),
                "Profile photo ID"
        );

        UserProfilePhoto existing =
                profilePhotoDao.findById(profilePhoto.getId());

        if (existing == null) {
            throw new IllegalArgumentException(
                    "Profile photo not found"
            );
        }

        validateFileMetadata(profilePhoto);

        profilePhoto.setUser(existing.getUser());

        return profilePhotoDao.update(profilePhoto);
    }

    @Override
    public boolean deleteProfilePhoto(long id) {

        ValidationUtil.validateId(
                id,
                "Profile photo ID"
        );

        UserProfilePhoto existing = profilePhotoDao.findById(id);

        if (existing == null) {
            return false;
        }
        /*
         * Delete the physical file first.
         *
         * If the file does not exist, delete() simply returns false.
         * The metadata can still safely be removed.
         */
        if (existing.getFilePath() != null
                && !existing.getFilePath().isBlank()) {
            fileStorageService.delete(existing.getFilePath());
        }
        profilePhotoDao.delete(id);
        return true;
    }

    @Override
    public UserProfilePhoto uploadProfilePhoto(long userId, byte[] fileData, String fileName, String contentType) {
        ValidationUtil.validateId(userId, "User ID");

        User user = userDao.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        FileValidationUtil.validateImage(fileData, fileName, contentType);

        UserProfilePhoto existing = profilePhotoDao.findByUserId(userId);
        String oldFilePath = null;
        if (existing != null) {
            oldFilePath = existing.getFilePath();
        }
        String storedPath = null;

        try {
            /*
             * Store the new physical file first.
             */
            storedPath = fileStorageService.store(fileData, fileName, contentType, "profile");

            String storedFileName = extractFileName(storedPath);

            /*
             * If the user already has a profile photo,
             * update its metadata.
             */
            if (existing != null) {
                existing.setFileName(fileName);
                existing.setStoredFileName(storedFileName);
                existing.setFilePath(storedPath);
                existing.setContentType(contentType);
                existing.setFileSize(fileData.length);

                UserProfilePhoto updated = profilePhotoDao.update(existing);

                if (oldFilePath != null
                        && !oldFilePath.equals(storedPath)) {

                    try {
                        fileStorageService.delete(oldFilePath);
                    } catch (Exception cleanupException) {

                        /*
                         * The new file and database metadata are already
                         * successfully updated.
                         *
                         * Do not delete the new file if cleanup of the
                         * old physical file fails.
                         */
                        System.err.println(
                                "Warning: Failed to delete old profile photo: "
                                        + oldFilePath
                        );

                        cleanupException.printStackTrace();
                    }
                }
                return updated;
            }

            /*
             * No existing profile photo.
             * Create a new metadata record.
             */
            UserProfilePhoto profilePhoto = new UserProfilePhoto();

            profilePhoto.setUser(user);
            profilePhoto.setFileName(fileName);
            profilePhoto.setStoredFileName(storedFileName);
            profilePhoto.setFilePath(storedPath);
            profilePhoto.setContentType(contentType);
            profilePhoto.setFileSize(fileData.length);

            return profilePhotoDao.save(profilePhoto);
        } catch (Exception e) {
            /*
             * If Oracle persistence fails after the file
             * was stored, remove the newly created file.
             */
            if (storedPath != null) {
                try {
                    fileStorageService.delete(storedPath);
                } catch (Exception cleanupException) {
                    e.addSuppressed(cleanupException);
                }
            }
            throw e;
        }
    }

    private String extractFileName(String filePath) {
        int lastSlash = filePath.lastIndexOf('/');

        if (lastSlash == -1) {
            return filePath;
        }

        return filePath.substring(lastSlash + 1);
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