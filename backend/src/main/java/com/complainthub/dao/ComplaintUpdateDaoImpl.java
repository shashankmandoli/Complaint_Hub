package com.complainthub.dao;

import com.complainthub.config.HibernateUtil;
import com.complainthub.entity.Complaint;
import com.complainthub.entity.ComplaintUpdate;
import com.complainthub.entity.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ComplaintUpdateDaoImpl implements ComplaintUpdateDao {

    private static final Logger log = LoggerFactory.getLogger(ComplaintUpdateDaoImpl.class);

    @Override
    public ComplaintUpdate save(ComplaintUpdate complaintUpdate) {

        Transaction transaction = null;

        try (Session session =
                     HibernateUtil.getSessionFactory().openSession()) {

            transaction = session.beginTransaction();

            Complaint complaint = session.get(
                    Complaint.class,
                    complaintUpdate.getComplaint().getId()
            );

            User updatedBy = session.get(
                    User.class,
                    complaintUpdate.getUpdatedBy().getId()
            );

            if (complaint == null) {
                throw new IllegalArgumentException("Complaint not found.");
            }
            if (updatedBy == null) {
                throw new IllegalArgumentException("UpdatedBy user not found.");
            }
            complaintUpdate.setComplaint(complaint);
            complaintUpdate.setUpdatedBy(updatedBy);

            session.persist(complaintUpdate);

            transaction.commit();
            log.info("Complaint update was saved successfully: {}", complaintUpdate);
            return complaintUpdate;
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback())
                transaction.rollback();
            log.error("Failed to save complaint update: {}", complaintUpdate, e);
            throw e;
        }
    }


    @Override
    public ComplaintUpdate findById(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(
                    ComplaintUpdate.class,
                    id
            );
        }
    }


    @Override
    public List<ComplaintUpdate> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM ComplaintUpdate",
                    ComplaintUpdate.class
            ).getResultList();
        }
    }


    @Override
    public List<ComplaintUpdate> findByComplaintId(long complaintId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM ComplaintUpdate " +
                                    "WHERE complaint.id = :complaintId " +
                                    "ORDER BY createdAt ASC",
                            ComplaintUpdate.class
                    ).setParameter("complaintId", complaintId).getResultList();
        }
    }


    @Override
    public List<ComplaintUpdate> findByUserId(long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM ComplaintUpdate " +
                                    "WHERE updatedBy.id = :userId " +
                                    "ORDER BY createdAt DESC",
                            ComplaintUpdate.class
                    ).setParameter("userId", userId).getResultList();
        }
    }


    @Override
    public List<ComplaintUpdate> findVisibleByComplaintId(long complaintId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM ComplaintUpdate " +
                                    "WHERE complaint.id = :complaintId " +
                                    "AND visibleToUser = true " +
                                    "ORDER BY createdAt ASC",
                            ComplaintUpdate.class
                    ).setParameter("complaintId", complaintId).getResultList();
        }
    }


    @Override
    public ComplaintUpdate update(ComplaintUpdate complaintUpdate) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            ComplaintUpdate updatedComplaintUpdate = session.merge(complaintUpdate);

            transaction.commit();
            log.info("Complaint updated successfully: {}", updatedComplaintUpdate);
            return updatedComplaintUpdate;
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                transaction.rollback();
            }
            log.error("Failed to update complaint status: ", e);
            throw e;
        }
    }
}