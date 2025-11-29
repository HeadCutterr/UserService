package util;

import model.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import userservice.BaseIntegrationTest;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            if (isTestEnvironment()) {
                sessionFactory = buildTestSessionFactory();
            } else {
                sessionFactory = buildProductionSessionFactory();
            }
        }
        return sessionFactory;
    }

    private static boolean isTestEnvironment() {
        // Определяем тестовую среду по наличию класса BaseIntegrationTest
        try {
            Class.forName("userservice.BaseIntegrationTest");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static SessionFactory buildTestSessionFactory() {
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.url", BaseIntegrationTest.getJdbcUrl());
        configuration.setProperty("hibernate.connection.username", BaseIntegrationTest.getUsername());
        configuration.setProperty("hibernate.connection.password", BaseIntegrationTest.getPassword());
        configuration.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        configuration.setProperty("hibernate.show_sql", "false");
        configuration.addAnnotatedClass(User.class);
        return configuration.buildSessionFactory();
    }

    private static SessionFactory buildProductionSessionFactory() {
        try {
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}