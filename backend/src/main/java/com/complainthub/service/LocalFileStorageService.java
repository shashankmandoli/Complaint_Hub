package com.complainthub.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class LocalFileStorageService implements FileStorageService {

    private final Path uploadRoot;

    public LocalFileStorageService() {

        String configuredPath =
                System.getProperty("complainthub.upload.root");

        if (configuredPath == null
                || configuredPath.isBlank()) {

            throw new IllegalStateException(
                    "Upload root is not configured. " +
                            "Set -Dcomplainthub.upload.root=<path>"
            );
        }

        this.uploadRoot = Paths.get(configuredPath)
                .toAbsolutePath()
                .normalize();
    }

    @Override
    public String store(
            byte[] fileData,
            String originalFileName,
            String contentType,
            String storageDirectory
    ) {

        if (fileData == null || fileData.length == 0) {
            throw new IllegalArgumentException(
                    "File data cannot be empty"
            );
        }

        if (originalFileName == null
                || originalFileName.isBlank()) {

            throw new IllegalArgumentException(
                    "Original file name is required"
            );
        }

        if (contentType == null
                || contentType.isBlank()) {

            throw new IllegalArgumentException(
                    "Content type is required"
            );
        }

        if (storageDirectory == null
                || storageDirectory.isBlank()) {

            throw new IllegalArgumentException(
                    "Storage directory is required"
            );
        }

        String extension =
                getFileExtension(originalFileName);

        String storedFileName =
                UUID.randomUUID() + extension;

        Path directory =
                uploadRoot
                        .resolve(storageDirectory)
                        .normalize();

        if (!directory.startsWith(uploadRoot)) {
            throw new IllegalArgumentException(
                    "Invalid storage directory"
            );
        }

        try {

            Files.createDirectories(directory);

            Path targetFile =
                    directory
                            .resolve(storedFileName)
                            .normalize();

            if (!targetFile.startsWith(uploadRoot)) {
                throw new IllegalArgumentException(
                        "Invalid file path"
                );
            }

            Files.write(
                    targetFile,
                    fileData
            );

            return uploadRoot
                    .relativize(targetFile)
                    .toString()
                    .replace("\\", "/");

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to store file",
                    e
            );
        }
    }

    @Override
    public boolean delete(String filePath) {

        if (filePath == null
                || filePath.isBlank()) {

            throw new IllegalArgumentException(
                    "File path is required"
            );
        }

        Path targetFile =
                resolveStoredFilePath(filePath);

        try {

            return Files.deleteIfExists(
                    targetFile
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to delete file",
                    e
            );
        }
    }

    private Path resolveStoredFilePath(
            String filePath
    ) {

        String normalizedPath =
                filePath.replace("\\", "/");

        if (normalizedPath.startsWith("/uploads/")) {

            normalizedPath =
                    normalizedPath.substring(
                            "/uploads/".length()
                    );

        } else if (normalizedPath.startsWith("uploads/")) {

            normalizedPath =
                    normalizedPath.substring(
                            "uploads/".length()
                    );

        } else {

            while (normalizedPath.startsWith("/")) {
                normalizedPath =
                        normalizedPath.substring(1);
            }
        }

        Path targetFile =
                uploadRoot
                        .resolve(normalizedPath)
                        .normalize();

        if (!targetFile.startsWith(uploadRoot)) {

            throw new IllegalArgumentException(
                    "Invalid file path"
            );
        }

        return targetFile;
    }

    private String getFileExtension(
            String fileName
    ) {

        int lastDot =
                fileName.lastIndexOf('.');

        if (lastDot == -1
                || lastDot == fileName.length() - 1) {

            return "";
        }

        return fileName
                .substring(lastDot)
                .toLowerCase();
    }
}