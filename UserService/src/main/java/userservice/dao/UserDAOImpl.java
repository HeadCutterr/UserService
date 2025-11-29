package userservice.dao;

import model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HibernateUtil;

import java.util.List;
import java.util.Optional;

public class UserDAOImpl implements UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAOImpl.class);

    @Override
    public User save(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            if(user.getId() == null || user.getId() == 0){
                user.initializeCreatedAt();
            }
            session.persist(user);
            transaction.commit();
            logger.info("User saved successfully: {}", user);
            return user;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Error saving user: {}", user, e);
            throw new RuntimeException("Failed to save user", e);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.get(User.class, id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            logger.error("Error finding user by ID: {}", id, e);
            throw new RuntimeException("Failed to find user", e);
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User", User.class).list();
        } catch (Exception e) {
            logger.error("Error fetching all users", e);
            throw new RuntimeException("Failed to fetch users", e);
        }
    }

    @Override
    public boolean update(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(user);
            transaction.commit();
            logger.info("User updated successfully: {}", user);
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Error updating user: {}", user, e);
            return false;
        }
    }

    @Override
    public boolean deleteById(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user != null) {
                session.remove(user);
                transaction.commit();
                logger.info("User deleted successfully: {}", id);
                return true;
            }
            return false;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Error deleting user by ID: {}", id, e);
            return false;
        }
    }
}
