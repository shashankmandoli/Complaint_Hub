package com.complainthub.dao;

import com.complainthub.config.HibernateUtil;
import com.complainthub.entity.Category;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CategoryDao {
    private static final Logger log = LoggerFactory.getLogger(CategoryDao.class);

    public void save(Category category){
        Transaction transaction = null;

        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            transaction = session.beginTransaction();
            session.persist(category);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback())
                transaction.rollback();
            log.error("Failed to save a category: ", e);
            throw e;
        }
    }

    public Category findById(long id){
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Category.class, id);
        }
    }

    public List<Category> findAll() {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Category ORDER BY name ASC",
                    Category.class
            ).getResultList();
        }
    }

    public List<Category> findAllActive() {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Category WHERE active = true ORDER BY name ASC",
                    Category.class
            ).getResultList();
        }
    }

    public void update(Category category){
        Transaction transaction = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            transaction = session.beginTransaction();
            session.merge(category);
            transaction.commit();
        } catch (Exception e){
            if(transaction != null)
                transaction.rollback();
            log.error("Failed to update category: ", e);
            throw e;
        }
    }

    public void deactivate(long id){
        Transaction transaction = null;

        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Category category = session.get(Category.class, id);

            if(category != null)
                category.setActive(false);

            transaction.commit();
        } catch (Exception e) {
            log.error("Deactivation of category failed: ", e);
            throw e;
        }
    }

    public void activate(long id){
        Transaction transaction = null;

        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            transaction = session.beginTransaction();
            Category category = session.get(Category.class, id);

            if (category != null)
                category.setActive(true);

            transaction.commit();
        } catch (Exception e) {
            log.error("Activation of category failed: ", e);
            throw e;
        }
    }
}
