package userservice.service;

import model.User;


import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(String name, String email, int age);
    List<User> getAllUsers();
    Optional<User> getUserById(Long id);
    boolean updateUser(Long id, String name, String email, Integer age);
    boolean deleteUser(Long id);
}