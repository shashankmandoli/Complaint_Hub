package com.complainthub.service;

import com.complainthub.entity.ComplaintUpdate;

import java.util.List;

public interface ComplaintUpdateService {
    ComplaintUpdate createUpdate(ComplaintUpdate complaintUpdate);
    ComplaintUpdate getUpdateById(long id);
    List<ComplaintUpdate> getAllUpdates();
    List<ComplaintUpdate> getUpdatesByComplaint(long complaintId);
    List<ComplaintUpdate> getUpdatesByUser(long userId);
    List<ComplaintUpdate> getVisibleUpdatesByComplaint(long complaintId);
    ComplaintUpdate updateUpdate(ComplaintUpdate complaintUpdate);
}
