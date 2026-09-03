package com.complainthub.service;

import com.complainthub.dao.*;
import com.complainthub.entity.Complaint;
import com.complainthub.entity.ComplaintUpdate;
import com.complainthub.entity.User;
import com.complainthub.entity.enums.UserRole;
import com.complainthub.util.ValidationUtil;

import java.util.List;

public class ComplaintUpdateServiceImpl
        implements ComplaintUpdateService {

    private final ComplaintUpdateDao complaintUpdateDao;
    private final ComplaintDao complaintDao;
    private final UserDao userDao;

    public ComplaintUpdateServiceImpl() {
        this.complaintUpdateDao = new ComplaintUpdateDaoImpl();
        this.complaintDao = new ComplaintDaoImpl();
        this.userDao = new UserDaoImpl();
    }

    @Override
    public ComplaintUpdate createUpdate(ComplaintUpdate complaintUpdate) {

        validateUpdate(complaintUpdate);

        long complaintId = complaintUpdate.getComplaint().getId();
        long updatedById = complaintUpdate.getUpdatedBy().getId();

        ValidationUtil.validateId(complaintId, "Complaint Id");
        ValidationUtil.validateId(updatedById, "Updated By Id");

        Complaint complaint = complaintDao.findById(complaintId);
        if (complaint == null)
            throw new IllegalArgumentException("Complaint not found.");

        User updatedBy = userDao.findById(updatedById);
        if (updatedBy == null)
            throw new IllegalArgumentException("Updated By user not found.");

        /*
         * Only agents and admins should be able
         * to add official complaint updates.
         */
        if (updatedBy.getRole() != UserRole.AGENT
                && updatedBy.getRole() != UserRole.ADMIN) {

            throw new IllegalArgumentException(
                    "Only an agent or admin can create a complaint update."
            );
        }

        complaintUpdate.setMessage(complaintUpdate.getMessage().trim());

        complaintUpdate.setComplaint(complaint);
        complaintUpdate.setUpdatedBy(updatedBy);

        complaintUpdateDao.save(complaintUpdate);

        return complaintUpdate;
    }

    @Override
    public ComplaintUpdate getUpdateById(long id) {
        ValidationUtil.validateId(id, "Complaint Update Id");
        return complaintUpdateDao.findById(id);
    }

    @Override
    public List<ComplaintUpdate> getAllUpdates() {
        return complaintUpdateDao.findAll();
    }

    @Override
    public List<ComplaintUpdate> getUpdatesByComplaint(long complaintId) {
        ValidationUtil.validateId(complaintId, "Complaint Id");
        return complaintUpdateDao.findByComplaintId(complaintId);
    }

    @Override
    public List<ComplaintUpdate> getUpdatesByUser(long userId) {
        ValidationUtil.validateId(userId, "User Id");
        return complaintUpdateDao.findByUserId(userId);
    }

    @Override
    public List<ComplaintUpdate> getVisibleUpdatesByComplaint(long complaintId) {
        ValidationUtil.validateId(complaintId, "Complaint Id");
        return complaintUpdateDao.findVisibleByComplaintId(complaintId);
    }

    @Override
    public ComplaintUpdate updateUpdate(ComplaintUpdate complaintUpdate) {
        validateUpdate(complaintUpdate);

        ValidationUtil.validateId(complaintUpdate.getId(), "Complaint Update Id");

        ComplaintUpdate existingUpdate = complaintUpdateDao.findById(complaintUpdate.getId());
        if (existingUpdate == null) {
            throw new IllegalArgumentException(
                    "Complaint update not found."
            );
        }

        /*
         * Complaint, author and creation time should
         * not be changed through an update.
         */
        complaintUpdate.setComplaint(existingUpdate.getComplaint());
        complaintUpdate.setUpdatedBy(existingUpdate.getUpdatedBy());
        complaintUpdate.setCreatedAt(existingUpdate.getCreatedAt());
        complaintUpdate.setMessage(complaintUpdate.getMessage().trim());

        return complaintUpdateDao.update(complaintUpdate
        );
    }

    // -------- Validate Update --------
    private void validateUpdate(
            ComplaintUpdate complaintUpdate) {

        if (complaintUpdate == null)
            throw new IllegalArgumentException("Complaint update cannot be null.");

        if (complaintUpdate.getComplaint() == null)
            throw new IllegalArgumentException("Complaint is required.");

        if (complaintUpdate.getUpdatedBy() == null)
            throw new IllegalArgumentException("Updated By user is required.");


        ValidationUtil.validateRequired(complaintUpdate.getMessage(), "Update Message");
        ValidationUtil.validateMaxLength(complaintUpdate.getMessage().trim(), 1000, "Update Message");
    }
}