package com.complainthub.service;

import com.complainthub.dao.ComplaintAttachmentDao;
import com.complainthub.dao.ComplaintAttachmentDaoImpl;
import com.complainthub.dao.ComplaintDao;
import com.complainthub.dao.ComplaintDaoImpl;
import com.complainthub.entity.Complaint;
import com.complainthub.entity.ComplaintAttachment;
import com.complainthub.util.FileValidationUtil;
import com.complainthub.util.ValidationUtil;

import java.util.List;

public class ComplaintAttachmentServiceImpl
        implements ComplaintAttachmentService {

    private final ComplaintAttachmentDao attachmentDao;
    private final ComplaintDao complaintDao;
    private final FileStorageService fileStorageService;

    public ComplaintAttachmentServiceImpl() {

        this.attachmentDao =
                new ComplaintAttachmentDaoImpl();

        this.complaintDao =
                new ComplaintDaoImpl();

        this.fileStorageService =
                new LocalFileStorageService();
    }

    @Override
    public ComplaintAttachment createAttachment(
            ComplaintAttachment attachment
    ) {

        if (attachment == null) {
            throw new IllegalArgumentException(
                    "Attachment cannot be null"
            );
        }

        if (attachment.getComplaint() == null) {
            throw new IllegalArgumentException(
                    "Complaint is required"
            );
        }

        long complaintId =
                attachment.getComplaint().getId();

        ValidationUtil.validateId(
                complaintId,
                "Complaint ID"
        );

        Complaint complaint =
                complaintDao.findById(complaintId);

        if (complaint == null) {
            throw new IllegalArgumentException(
                    "Complaint not found"
            );
        }

        validateFileMetadata(attachment);

        attachment.setComplaint(complaint);

        return attachmentDao.save(attachment);
    }

    @Override
    public ComplaintAttachment getAttachmentById(long id) {

        ValidationUtil.validateId(
                id,
                "Attachment ID"
        );

        return attachmentDao.findById(id);
    }

    @Override
    public List<ComplaintAttachment> getAllAttachments() {
        return attachmentDao.findAll();
    }

    @Override
    public List<ComplaintAttachment> getAttachmentsByComplaint(
            long complaintId
    ) {

        ValidationUtil.validateId(
                complaintId,
                "Complaint ID"
        );

        return attachmentDao.findByComplaintId(
                complaintId
        );
    }

    @Override
    public boolean deleteAttachment(long id) {

        ValidationUtil.validateId(
                id,
                "Attachment ID"
        );

        ComplaintAttachment existing =
                attachmentDao.findById(id);

        if (existing == null) {
            return false;
        }

        /*
         * Delete physical file first.
         */
        if (existing.getFilePath() != null
                && !existing.getFilePath().isBlank()) {

            fileStorageService.delete(
                    existing.getFilePath()
            );
        }

        /*
         * Then remove Oracle metadata.
         */
        attachmentDao.delete(id);

        return true;
    }

    @Override
    public void cleanupAttachments(long complaintId) {

        ValidationUtil.validateId(
                complaintId,
                "Complaint ID"
        );

        Complaint complaint =
                complaintDao.findById(complaintId);

        if (complaint == null) {
            throw new IllegalArgumentException(
                    "Complaint not found"
            );
        }

        /*
         * Fetch the attachments first because the DAO's
         * bulk delete only removes database metadata.
         */
        List<ComplaintAttachment> attachments =
                attachmentDao.findByComplaintId(
                        complaintId
                );

        /*
         * Delete physical files.
         */
        for (ComplaintAttachment attachment : attachments) {

            if (attachment.getFilePath() != null
                    && !attachment.getFilePath().isBlank()) {

                fileStorageService.delete(
                        attachment.getFilePath()
                );
            }
        }

        /*
         * Finally delete all metadata from Oracle.
         */
        attachmentDao.deleteByComplaintId(
                complaintId
        );
    }

    @Override
    public ComplaintAttachment uploadAttachment(
            long complaintId,
            byte[] fileData,
            String fileName,
            String contentType
    ) {

        ValidationUtil.validateId(
                complaintId,
                "Complaint ID"
        );

        Complaint complaint =
                complaintDao.findById(complaintId);

        if (complaint == null) {
            throw new IllegalArgumentException(
                    "Complaint not found"
            );
        }

        FileValidationUtil.validateImage(
                fileData,
                fileName,
                contentType
        );

        String storageDirectory =
                "complaints/" + complaintId;

        String storedPath = null;

        try {

            /*
             * Store physical file.
             */
            storedPath = fileStorageService.store(
                    fileData,
                    fileName,
                    contentType,
                    storageDirectory
            );

            String storedFileName =
                    extractFileName(storedPath);

            /*
             * Create Oracle metadata.
             */
            ComplaintAttachment attachment =
                    new ComplaintAttachment();

            attachment.setComplaint(complaint);
            attachment.setFileName(fileName);
            attachment.setStoredFileName(
                    storedFileName
            );
            attachment.setFilePath(storedPath);
            attachment.setContentType(contentType);
            attachment.setFileSize(fileData.length);

            return attachmentDao.save(
                    attachment
            );

        } catch (Exception e) {

            /*
             * If Oracle persistence fails,
             * remove the physical file.
             */
            if (storedPath != null) {

                try {
                    fileStorageService.delete(
                            storedPath
                    );
                } catch (Exception cleanupException) {

                    e.addSuppressed(
                            cleanupException
                    );
                }
            }

            throw e;
        }
    }

    private String extractFileName(String filePath) {

        int lastSlash =
                filePath.lastIndexOf('/');

        if (lastSlash == -1) {
            return filePath;
        }

        return filePath.substring(
                lastSlash + 1
        );
    }

    // -------- Validate Method --------

    private void validateFileMetadata(
            ComplaintAttachment attachment
    ) {

        ValidationUtil.validateRequired(
                attachment.getFileName(),
                "File name"
        );

        ValidationUtil.validateMaxLength(
                attachment.getFileName(),
                255,
                "File name"
        );

        ValidationUtil.validateRequired(
                attachment.getStoredFileName(),
                "Stored file name"
        );

        ValidationUtil.validateMaxLength(
                attachment.getStoredFileName(),
                255,
                "Stored file name"
        );

        ValidationUtil.validateRequired(
                attachment.getFilePath(),
                "File path"
        );

        ValidationUtil.validateMaxLength(
                attachment.getFilePath(),
                1000,
                "File path"
        );

        ValidationUtil.validateRequired(
                attachment.getContentType(),
                "Content type"
        );

        ValidationUtil.validateMaxLength(
                attachment.getContentType(),
                100,
                "Content type"
        );

        if (attachment.getFileSize() <= 0) {
            throw new IllegalArgumentException(
                    "File size must be greater than zero"
            );
        }
    }
}