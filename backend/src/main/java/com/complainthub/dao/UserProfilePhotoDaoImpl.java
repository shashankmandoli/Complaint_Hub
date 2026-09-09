package com.complainthub.dao;

import com.complainthub.config.HibernateUtil;
import com.complainthub.entity.UserProfilePhoto;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class UserProfilePhotoDaoImpl implements UserProfilePhotoDao {

    @Override
    public UserProfilePhoto save(UserProfilePhoto profilePhoto) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.persist(profilePhoto);

            transaction.commit();

            return profilePhoto;
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null && transaction.getStatus().canRollback())
                transaction.rollback();
            throw e;
        }
    }

    @Override
    public UserProfilePhoto findById(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(UserProfilePhoto.class, id);
        }
    }

    @Override
    public UserProfilePhoto findByUserId(long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM UserProfilePhoto p WHERE p.user.id = :userId",
                            UserProfilePhoto.class
                    ).setParameter("userId", userId).uniqueResult();
        }
    }

    @Override
    public List<UserProfilePhoto> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM UserProfilePhoto",
                            UserProfilePhoto.class
                    ).getResultList();
        }
    }

    @Override
    public UserProfilePhoto update(UserProfilePhoto profilePhoto) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            UserProfilePhoto updatedPhoto = session.merge(profilePhoto);

            transaction.commit();
            return updatedPhoto;
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback())
                transaction.rollback();
            throw e;
        }
    }

    @Override
    public void delete(long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            UserProfilePhoto profilePhoto = session.get(UserProfilePhoto.class, id);

            if (profilePhoto != null)
                session.remove(profilePhoto);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback())
                transaction.rollback();
            throw e;
        }
    }
}