package com.complainthub.config;

import com.complainthub.entity.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class HibernateUtil {
    private static final SessionFactory factory;

    static{
        try {
            Properties databaseProperties = new Properties();

            try (InputStream inputStream =
                         HibernateUtil.class
                                 .getClassLoader()
                                 .getResourceAsStream("database.properties")) {

                if (inputStream == null) {
                    throw new RuntimeException(
                            "database.properties file not found"
                    );
                }

                databaseProperties.load(inputStream);
            }

            Configuration config =
                    new Configuration().configure();

            config.addAnnotatedClass(User.class);
            config.addAnnotatedClass(Category.class);
            config.addAnnotatedClass(Complaint.class);
            config.addAnnotatedClass(ComplaintAssignment.class);
            config.addAnnotatedClass(ComplaintUpdate.class);
            config.addAnnotatedClass(UserProfilePhoto.class);
            config.addAnnotatedClass(ComplaintAttachment.class);

            config.setProperty(
                    "hibernate.connection.url",
                    databaseProperties.getProperty("db.url")
            );

            config.setProperty(
                    "hibernate.connection.username",
                    databaseProperties.getProperty("db.username")
            );

            config.setProperty(
                    "hibernate.connection.password",
                    databaseProperties.getProperty("db.password")
            );

            factory =
                    config.buildSessionFactory();

        } catch (IOException exception) {
            throw new RuntimeException(
                    "Failed to load database configuration",
                    exception
            );
        } catch (Exception exception) {
            throw new RuntimeException(
                    "Failed to initialize Hibernate SessionFactory",
                    exception
            );
        }
    }

    private HibernateUtil(){
    }

    public static SessionFactory getSessionFactory(){
        return factory;
    }

    public static void shutdown(){
        factory.close();
    }
}
