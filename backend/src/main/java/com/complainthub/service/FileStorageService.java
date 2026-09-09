package com.complainthub.service;

public interface FileStorageService {
    String store(byte[] fileData, String originalFileName, String contentType, String storageDirectory);
    boolean delete(String filePath);
}
