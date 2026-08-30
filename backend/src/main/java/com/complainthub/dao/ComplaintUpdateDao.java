package com.complainthub.dao;

import com.complainthub.entity.ComplaintUpdate;

import java.util.List;

public interface ComplaintUpdateDao {
    ComplaintUpdate save(ComplaintUpdate complaintUpdate);
    ComplaintUpdate findById(long id);
    List<ComplaintUpdate> findAll();
    ComplaintUpdate update(ComplaintUpdate complaintUpdate);
    List<ComplaintUpdate> findByComplaintId(long complaintId);
    List<ComplaintUpdate> findByUserId(long userId);
    List<ComplaintUpdate> findVisibleByComplaintId(long complaintId);
}
