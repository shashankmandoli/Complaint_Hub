package com.complainthub.service;

import com.complainthub.entity.ComplaintAssignment;

import java.util.List;

public interface ComplaintAssignmentService {
    ComplaintAssignment createAssignment(ComplaintAssignment assignment);
    ComplaintAssignment getAssignmentById(long id);
    List<ComplaintAssignment> getAllAssignments();
    List<ComplaintAssignment> getAssignmentsByComplaint(long complaintId);
    List<ComplaintAssignment> getAssignmentsByAgent(long agentId);
    ComplaintAssignment getActiveAssignmentByComplaint(long complaintId);
    ComplaintAssignment updateAssignment(ComplaintAssignment assignment);
    boolean deactivateAssignment(long assignmentId);
}
