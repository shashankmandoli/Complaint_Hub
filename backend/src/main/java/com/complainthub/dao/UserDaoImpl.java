package com.complainthub.dao;

import com.complainthub.config.HibernateUtil;
import com.complainthub.entity.User;
import com.complainthub.entity.enums.UserRole;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserDaoImpl implements UserDao{
    private static final Logger log = LoggerFactory.getLogger(UserDaoImpl.class);

    @Override
    public User save(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.persist(user);

            transaction.commit();
            log.info("User saved successfully: {}",user);
            return user;
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                transaction.rollback();
            }
            log.error("Failed to saved the user: ", e);
            throw e;
        }
    }

    @Override
    public User findById(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(
                    User.class,
                    id
            );
        }
    }

    @Override
    public User findByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM User WHERE email = :email",
                            User.class
                    ).setParameter("email", email).uniqueResult();
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM User ORDER BY id ASC",
                    User.class
            ).getResultList();
        }
    }

    @Override
    public List<User> findByRole(UserRole role) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM User WHERE role = :role ORDER BY id ASC",
                    User.class
            ).setParameter("role", role).getResultList();
        }
    }

    @Override
    public User update(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            User updatedUser = session.merge(user);

            transaction.commit();
            log.info("User updated successfully: {}", updatedUser);
            return updatedUser;
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback())
                transaction.rollback();
            log.error("Failed to update the user: ", e);
            throw e;
        }
    }
}
