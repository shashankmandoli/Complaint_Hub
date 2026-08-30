package com.complainthub.dao;

import com.complainthub.config.HibernateUtil;
import com.complainthub.entity.ComplaintAssignment;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ComplaintAssignmentDaoImpl implements ComplaintAssignmentDao{
    private static final Logger log = LoggerFactory.getLogger(ComplaintAssignmentDaoImpl.class);

    @Override
    public ComplaintAssignment save(ComplaintAssignment assignment) {
        Transaction transaction = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            transaction = session.beginTransaction();

            session.persist(assignment);

            transaction.commit();
            log.info("Complaint assignment saved successfully with id: {}", assignment.getId());
            return assignment;
        } catch (Exception e){
            if(transaction != null && transaction.getStatus().canRollback())
                transaction.rollback();
            log.error("Failed to save complaint assignment: {}", assignment, e);
            throw e;
        }
    }

    @Override
    public ComplaintAssignment findById(long id) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            return session.get(
                    ComplaintAssignment.class, id
            );
        } catch (Exception e) {
            log.error("Failed to find complaint assignment with id: {}", id, e);
            throw e;
        }
    }

    @Override
    public List<ComplaintAssignment> findAll() {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM ComplaintAssignment",
                    ComplaintAssignment.class
            ).getResultList();
        } catch (Exception e) {
            log.error("Failed to fetch all assigned complaints: ", e);
            throw e;
        }
    }

    @Override
    public ComplaintAssignment update(ComplaintAssignment assignment) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            ComplaintAssignment updatedAssignment = session.merge(assignment);

            transaction.commit();
            log.info("Complaint assignment updated successfully with id: {}", updatedAssignment.getId());

            return updatedAssignment;
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback())
                transaction.rollback();
            log.error("Failed to update complaint assignment: {}", assignment, e);
            throw e;
        }
    }

    @Override
    public List<ComplaintAssignment> findByComplaintId(long complaintId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM ComplaintAssignment ca " +
                                    "WHERE ca.complaint.id = :complaintId",
                            ComplaintAssignment.class
                    )
                    .setParameter("complaintId", complaintId).getResultList();
        } catch (Exception e) {
            log.error("Failed to find assignments for complaint id: {}", complaintId);
            throw e;
        }
    }

    @Override
    public List<ComplaintAssignment> findByAgentId(long agentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM ComplaintAssignment ca " +
                                    "WHERE ca.agent.id = :agentId",
                            ComplaintAssignment.class
                    ).setParameter("agentId", agentId).getResultList();
        } catch (Exception e) {
            log.error("Failed to find assignments for agent id: {}", agentId, e);
            throw e;
        }
    }

    @Override
    public ComplaintAssignment findActiveAssignmentByComplaintId(long complaintId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<ComplaintAssignment> assignments =
                    session.createQuery(
                                    "FROM ComplaintAssignment ca " +
                                            "WHERE ca.complaint.id = :complaintId " +
                                            "AND ca.active = true",
                                    ComplaintAssignment.class
                            ).setParameter("complaintId", complaintId).getResultList();

            if (assignments.isEmpty()) {
                return null;
            }
            return assignments.getFirst();
        } catch (Exception e) {
            log.error("Failed to find active assignment for complaint id: {}", complaintId, e);
            throw e;
        }
    }
}
