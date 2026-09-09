package com.complainthub.util;

import java.util.Set;

public final class FileValidationUtil {

    private FileValidationUtil() {
    }

    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of(
            ".jpg",
            ".jpeg",
            ".png",
            ".webp"
    );

    public static void validateImage(byte[] fileData, String fileName, String contentType) {
        validateFileData(fileData);
        validateFileSize(fileData.length);
        validateContentType(contentType);
        validateFileExtension(fileName);
    }

    public static void validateFileData(byte[] fileData) {
        if (fileData == null || fileData.length == 0) {
            throw new IllegalArgumentException("File cannot be empty");
        }
    }

    public static void validateFileSize(long fileSize) {
        if (fileSize > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException("Image size cannot exceed 5 MB");
        }
    }

    public static void validateContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("Content type is required");
        }
        if (!ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Unsupported image type: " + contentType);
        }
    }

    public static void validateFileExtension(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("File name is required");
        }

        String lowerCaseFileName = fileName.toLowerCase();
        boolean validExtension = ALLOWED_IMAGE_EXTENSIONS.stream().anyMatch(lowerCaseFileName::endsWith);

        if (!validExtension) {
            throw new IllegalArgumentException("Unsupported image file extension");
        }
    }

    public static long getMaxImageSize() {
        return MAX_IMAGE_SIZE;
    }
}