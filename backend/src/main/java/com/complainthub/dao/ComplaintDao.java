package com.complainthub.dao;

import com.complainthub.entity.Complaint;
import com.complainthub.entity.enums.ComplaintPriority;
import com.complainthub.entity.enums.ComplaintStatus;

import java.util.List;

public interface ComplaintDao {
    Complaint save(Complaint complaint);
    Complaint findById(long id);
    List<Complaint> findAll();
    Complaint update(Complaint complaint);
    List<Complaint> findByUserId(long userId);
//    List<Complaint> findByAssignedAgentId(long agentId);
    List<Complaint> findByCategoryId(long categoryId);
    List<Complaint> findByStatus(ComplaintStatus status);
    List<Complaint> findByPriority(ComplaintPriority priority);
//    Complaint assignAgent(long agentId, long complaintId);
    Complaint updateStatus(long complaintId, ComplaintStatus status);
    Complaint updatePriority(long complaintId, ComplaintPriority priority);
}
