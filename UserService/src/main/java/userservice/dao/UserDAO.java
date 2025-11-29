package userservice.dao;

import model.User;

import java.util.List;
import java.util.Optional;

public interface UserDAO {
    User save(User user);
    Optional<User> findById(Long id);
    List<User> findAll();
    boolean update(User user);
    boolean deleteById(Long id);
}