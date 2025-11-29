package userservice.dao;

import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;
import userservice.BaseIntegrationTest;
import util.HibernateUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class UserDAOTest extends BaseIntegrationTest {
    private UserDAO userDAO;
    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2023, 1, 1, 12, 0);

    @BeforeEach
    void setUp() {
        userDAO = new UserDAOImpl();
    }

    @AfterEach
    void tearDown() {
        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.createNativeQuery("TRUNCATE TABLE users RESTART IDENTITY CASCADE", Integer.class).executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Test
    @DisplayName("should save and find user by ID")
    void saveAndFindById() {
        User user = new User("Test User", "test@example.com", 30);
        user.setCreatedAt(FIXED_TIME);

        User savedUser = userDAO.save(user);
        Optional<User> foundUser = userDAO.findById(savedUser.getId());

        assertTrue(foundUser.isPresent());
        assertEquals(FIXED_TIME, foundUser.get().getCreatedAt());
    }

    @Test
    @DisplayName("should find all users")
    void findAll() {
        userDAO.save(new User("User1", "user1@example.com", 25));
        userDAO.save(new User("User2", "user2@example.com", 35));

        List<User> users = userDAO.findAll();

        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> "User1".equals(u.getName())));
        assertTrue(users.stream().anyMatch(u -> "User2".equals(u.getName())));
    }

    @Test
    @DisplayName("should update user")
    void updateUser() {
        User user = userDAO.save(new User("Old Name", "old@example.com", 30));

        user.setName("New Name");
        user.setEmail("new@example.com");
        user.setAge(40);
        boolean updated = userDAO.update(user);

        assertTrue(updated);
        Optional<User> updatedUser = userDAO.findById(user.getId());
        assertTrue(updatedUser.isPresent());
        assertEquals("New Name", updatedUser.get().getName());
        assertEquals("new@example.com", updatedUser.get().getEmail());
        assertEquals(40, updatedUser.get().getAge());
    }

    @Test
    @DisplayName("should delete user by ID")
    void deleteById() {
        User user = userDAO.save(new User("ToDelete", "delete@example.com", 28));

        boolean deleted = userDAO.deleteById(user.getId());

        assertTrue(deleted);
        assertFalse(userDAO.findById(user.getId()).isPresent());
    }

    @Test
    @DisplayName("should return empty optional when user not found")
    void findById_NotFound() {
        Optional<User> result = userDAO.findById(999L);

        assertFalse(result.isPresent());
    }
}