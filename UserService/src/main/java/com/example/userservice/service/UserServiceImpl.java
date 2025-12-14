package com.example.userservice.service;

import com.example.userservice.dto.UserCreateDTO;
import com.example.userservice.dto.UserDTO;
import com.example.userservice.event.UserEvent;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.userRepository = userRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @Transactional
    public UserDTO createUser(UserCreateDTO dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("User with email " + dto.email() + " already exists");
        }

        User user = new User(dto.name(), dto.email(), dto.age());
        User savedUser = userRepository.save(user);

        UserEvent event = new UserEvent(savedUser.getEmail(), UserEvent.EventType.CREATE);
        kafkaTemplate.send("user-events", event);

        return convertToDTO(savedUser);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserCreateDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (!user.getEmail().equals(dto.email()) && userRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("User with email " + dto.email() + " already exists");
        }

        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setAge(dto.age());

        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    @Override
    @Transactional
    public boolean deleteUser(Long id) {
        Optional<User> userOpt = userRepository.findById(id);

        if (userOpt.isEmpty()) {
            log.warn("Attempt to delete non-existent user with id: {}", id);
            return false;
        }

        User user = userOpt.get();

        userRepository.deleteById(id);

        try {
            UserEvent event = new UserEvent(user.getEmail(), UserEvent.EventType.DELETE);
            kafkaTemplate.send("user-events", event);
            log.info("Sent DELETE event for user id: {}, email: {}", id, user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send Kafka event for user deletion (id: {}). Rolling back transaction.", id, e);
            throw new RuntimeException("Failed to send Kafka event", e);
        }

        return true;
    }

    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge(),
                user.getCreatedAt()
        );
    }
}