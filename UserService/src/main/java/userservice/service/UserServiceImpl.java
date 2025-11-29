package userservice.service;

import model.User;
import userservice.dao.UserDAO;
import userservice.dao.UserDAOImpl;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {
    private final UserDAO userDAO;

    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public UserServiceImpl() {
        this(new UserDAOImpl());
    }

    @Override
    public User createUser(String name, String email, int age) {
        validateName(name);
        validateEmail(email);
        validateAge(age);

        User user = new User(name, email, age);
        user.initializeCreatedAt();
        return userDAO.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        return userDAO.findById(id);
    }

    @Override
    public boolean updateUser(Long id, String name, String email, Integer age) {
        Optional<User> optionalUser = getUserById(id);
        if (optionalUser.isEmpty()) {
            return false;
        }

        User user = optionalUser.get();

        if (name != null && !name.isEmpty()) {
            validateName(name);
            user.setName(name);
        }

        if (email != null && !email.isEmpty()) {
            validateEmail(email);
            user.setEmail(email);
        }

        if (age != null) {
            validateAge(age);
            user.setAge(age);
        }

        return userDAO.update(user);
    }

    @Override
    public boolean deleteUser(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        return userDAO.deleteById(id);
    }

    private void validateName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (!name.matches("[a-zA-Zа-яА-ЯёЁ\\s'-]{2,50}")) {
            throw new IllegalArgumentException("Name must contain only letters, spaces, hyphens or apostrophes (2-50 chars)");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email format (example: user@mail.ru)");
        }
    }

    private void validateAge(int age) {
        if (age < 1 || age > 120) {
            throw new IllegalArgumentException("Age must be between 1 and 120");
        }
    }
}