package com.complainthub.dao;

import com.complainthub.config.HibernateUtil;
import com.complainthub.entity.Complaint;
import com.complainthub.entity.enums.ComplaintPriority;
import com.complainthub.entity.enums.ComplaintStatus;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ComplaintDaoImpl implements ComplaintDao {
    private static final Logger log = LoggerFactory.getLogger(ComplaintDaoImpl.class);

    @Override
    public Complaint save(Complaint complaint) {
        Transaction transaction = null;

        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.persist(complaint);

            transaction.commit();

            log.info("Complaint saved successfully with id: {}", complaint.getId());

            return complaint;
        } catch (Exception e) {
            if(transaction != null && transaction.getStatus().canRollback())
                transaction.rollback();
            log.error("Failed to save complaint: ", e);
            throw e;
        }
    }

    @Override
    public Complaint findById(long id) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Complaint.class, id);
        }
    }

    @Override
    public List<Complaint> findAll() {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Complaint",
                    Complaint.class
            ).getResultList();
        }
    }

    @Override
    public List<Complaint> findByUserId(long userId) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            return session.createQuery(
                    "FROM Complaint c WHERE c.user.id = :userId",
                    Complaint.class
            ).setParameter("userId", userId).getResultList();
        }
    }

//    @Override
//    public List<Complaint> findByAssignedAgentId(long agentId) {
//        return List.of();
//    }

    @Override
    public List<Complaint> findByCategoryId(long categoryId) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Complaint c where c.category.id = :categoryId",
                    Complaint.class
            ).setParameter("categoryId", categoryId).getResultList();
        }
    }

    @Override
    public List<Complaint> findByStatus(ComplaintStatus status) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Complaint c WHERE c.status = :status",
                    Complaint.class
            ).setParameter("status", status).getResultList();
        }
    }

    @Override
    public List<Complaint> findByPriority(ComplaintPriority priority) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Complaint c WHERE c.priority = :priority",
                    Complaint.class
            ).setParameter("priority", priority).getResultList();
        }
    }

    @Override
    public Complaint update(Complaint complaint) {
        Transaction transaction = null;

        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            transaction = session.beginTransaction();
            Complaint updatedComplaint = session.merge(complaint);
            transaction.commit();

            log.info("Complaint updated successfully with id: {}", updatedComplaint.getId());
            return updatedComplaint;
        } catch (Exception e) {
            if(transaction != null && transaction.getStatus().canRollback())
                transaction.rollback();
            log.error("Failed to update complaint: ", e);
            throw e;
        }
    }

//    @Override
//    public Complaint assignAgent(long agentId, long complaintId) {
//        return null;
//    }

    @Override
    public Complaint updateStatus(long complaintId, ComplaintStatus status) {
        Transaction transaction = null;

        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Complaint complaint = session.get(Complaint.class, complaintId);

            if(complaint == null)
                return null;

            complaint.setStatus(status);
            transaction.commit();

            log.info("Complaint status updated successfully... Complaint id: {}, Status: {}",
                    complaintId, status);
            return complaint;
        } catch (Exception e) {
            if(transaction != null && transaction.getStatus().canRollback())
                transaction.rollback();
            log.error("Failed to update complaint status: ", e);
            throw e;
        }
    }

    @Override
    public Complaint updatePriority(long complaintId, ComplaintPriority priority) {
        Transaction transaction = null;

        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            transaction = session.beginTransaction();
            Complaint complaint = session.get(Complaint.class, complaintId);

            if(complaint == null)
                return null;

            complaint.setPriority(priority);

            transaction.commit();
            log.info("Complaint priority was updated successfully... Complaint id: {}, Priority: {}",
                    complaintId, priority);
            return complaint;
        } catch (Exception e) {
            if(transaction != null && transaction.getStatus().canRollback())
                transaction.rollback();
            log.error("Failed to update complaint priority: ", e);
            throw e;
        }
    }
}
