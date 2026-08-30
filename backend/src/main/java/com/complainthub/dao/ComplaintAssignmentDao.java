package com.complainthub.dao;

import com.complainthub.entity.Complaint;
import com.complainthub.entity.ComplaintAssignment;

import java.util.List;

public interface ComplaintAssignmentDao {
    ComplaintAssignment save(ComplaintAssignment assignment);
    ComplaintAssignment findById(long id);
    List<ComplaintAssignment> findAll();
    ComplaintAssignment update(ComplaintAssignment assignment);
    List<ComplaintAssignment> findByComplaintId(long complaintId);
    List<ComplaintAssignment> findByAgentId(long agentId);
    ComplaintAssignment findActiveAssignmentByComplaintId(long complaintId);
}
