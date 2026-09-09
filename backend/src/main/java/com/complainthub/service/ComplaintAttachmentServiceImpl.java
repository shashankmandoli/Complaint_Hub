package com.complainthub.service;

import com.complainthub.dao.ComplaintAttachmentDao;
import com.complainthub.dao.ComplaintAttachmentDaoImpl;
import com.complainthub.dao.ComplaintDao;
import com.complainthub.dao.ComplaintDaoImpl;
import com.complainthub.entity.Complaint;
import com.complainthub.entity.ComplaintAttachment;
import com.complainthub.util.ValidationUtil;

import java.util.List;

public class ComplaintAttachmentServiceImpl implements ComplaintAttachmentService {

    private final ComplaintAttachmentDao attachmentDao;
    private final ComplaintDao complaintDao;

    public ComplaintAttachmentServiceImpl() {
        this.attachmentDao =
                new ComplaintAttachmentDaoImpl();

        this.complaintDao =
                new ComplaintDaoImpl();
    }

    @Override
    public ComplaintAttachment createAttachment(ComplaintAttachment attachment) {
        if (attachment == null) {
            throw new IllegalArgumentException("Attachment cannot be null");
        }

        if (attachment.getComplaint() == null) {
            throw new IllegalArgumentException("Complaint is required");
        }

        long complaintId = attachment.getComplaint().getId();

        ValidationUtil.validateId(complaintId, "Complaint ID");

        Complaint complaint = complaintDao.findById(complaintId);
        if (complaint == null) {
            throw new IllegalArgumentException("Complaint not found");
        }

        validateFileMetadata(attachment);
        attachment.setComplaint(complaint);
        return attachmentDao.save(attachment);
    }

    @Override
    public ComplaintAttachment getAttachmentById(long id) {
        ValidationUtil.validateId(id, "Attachment ID");
        return attachmentDao.findById(id);
    }

    @Override
    public List<ComplaintAttachment> getAllAttachments() {
        return attachmentDao.findAll();
    }

    @Override
    public List<ComplaintAttachment> getAttachmentsByComplaint(long complaintId) {
        ValidationUtil.validateId(complaintId, "Complaint ID");
        return attachmentDao.findByComplaintId(complaintId);
    }

    @Override
    public boolean deleteAttachment(long id) {
        ValidationUtil.validateId(id, "Attachment ID");

        ComplaintAttachment existing = attachmentDao.findById(id);
        if (existing == null) {
            return false;
        }

        attachmentDao.delete(id);
        return true;
    }

    @Override
    public void cleanupAttachments(long complaintId) {
        ValidationUtil.validateId(complaintId, "Complaint ID");

        Complaint complaint = complaintDao.findById(complaintId);
        if (complaint == null) {
            throw new IllegalArgumentException("Complaint not found");
        }

        attachmentDao.deleteByComplaintId(complaintId);
    }

    // -------- Validate Method --------
    private void validateFileMetadata(ComplaintAttachment attachment) {
        ValidationUtil.validateRequired(attachment.getFileName(), "File name");
        ValidationUtil.validateMaxLength(attachment.getFileName(), 255, "File name");
        ValidationUtil.validateRequired(attachment.getStoredFileName(), "Stored file name");
        ValidationUtil.validateMaxLength(attachment.getStoredFileName(), 255, "Stored file name");
        ValidationUtil.validateRequired(attachment.getFilePath(), "File path");
        ValidationUtil.validateMaxLength(attachment.getFilePath(), 1000, "File path");
        ValidationUtil.validateRequired(attachment.getContentType(), "Content type");
        ValidationUtil.validateMaxLength(attachment.getContentType(), 100, "Content type");

        if (attachment.getFileSize() <= 0) {
            throw new IllegalArgumentException(
                    "File size must be greater than zero"
            );
        }
    }
}