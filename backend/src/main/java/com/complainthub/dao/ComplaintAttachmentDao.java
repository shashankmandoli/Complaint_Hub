package com.complainthub.dao;

import com.complainthub.entity.ComplaintAttachment;

import java.util.List;

public interface ComplaintAttachmentDao {
    ComplaintAttachment save(ComplaintAttachment attachment);
    ComplaintAttachment findById(long id);
    List<ComplaintAttachment> findAll();
    List<ComplaintAttachment> findByComplaintId(long complaintId);
    void delete(long id);
    void deleteByComplaintId(long complaintId);
}
