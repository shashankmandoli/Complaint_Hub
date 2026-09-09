package com.complainthub.dao;

import com.complainthub.config.HibernateUtil;
import com.complainthub.entity.ComplaintAttachment;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class ComplaintAttachmentDaoImpl implements ComplaintAttachmentDao {

    @Override
    public ComplaintAttachment save(ComplaintAttachment attachment) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.persist(attachment);

            transaction.commit();
            return attachment;
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback())
                transaction.rollback();
            throw e;
        }
    }

    @Override
    public ComplaintAttachment findById(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(ComplaintAttachment.class, id);
        }
    }

    @Override
    public List<ComplaintAttachment> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM ComplaintAttachment",
                            ComplaintAttachment.class
                    ).getResultList();
        }
    }

    @Override
    public List<ComplaintAttachment> findByComplaintId(long complaintId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            """
                            FROM ComplaintAttachment a
                            WHERE a.complaint.id = :complaintId
                            ORDER BY a.createdAt ASC
                            """,
                            ComplaintAttachment.class
                    ).setParameter("complaintId", complaintId).getResultList();
        }
    }

    @Override
    public void delete(long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            ComplaintAttachment attachment = session.get(ComplaintAttachment.class, id);

            if (attachment != null)
                session.remove(attachment);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback())
                transaction.rollback();
            throw e;
        }
    }

    @Override
    public void deleteByComplaintId(long complaintId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.createMutationQuery(
                            """
                            DELETE FROM ComplaintAttachment a
                            WHERE a.complaint.id = :complaintId
                            """
                    ).setParameter("complaintId", complaintId).executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback())
                transaction.rollback();
            throw e;
        }
    }
}