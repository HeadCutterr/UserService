package com.example.userservice.service;

import com.example.userservice.dto.UserCreateDTO;
import com.example.userservice.dto.UserDTO;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDTO createUser(UserCreateDTO dto);

    List<UserDTO> getAllUsers();

    Optional<UserDTO> getUserById(Long id);

    UserDTO updateUser(Long id, UserCreateDTO dto);

    boolean deleteUser(Long id);
}