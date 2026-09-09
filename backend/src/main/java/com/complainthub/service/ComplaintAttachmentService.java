package com.complainthub.service;

import com.complainthub.entity.ComplaintAttachment;

import java.util.List;

public interface ComplaintAttachmentService {
    ComplaintAttachment createAttachment(ComplaintAttachment attachment);
    ComplaintAttachment getAttachmentById(long id);
    List<ComplaintAttachment> getAllAttachments();
    List<ComplaintAttachment> getAttachmentsByComplaint(long complaintId);
    boolean deleteAttachment(long id);void cleanupAttachments(long complaintId);
    ComplaintAttachment uploadAttachment(
            long complaintId,
            byte[] fileData,
            String fileName,
            String contentType
    );
}