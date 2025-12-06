package com.example.userservice.service;

import com.example.userservice.dto.UserCreateDTO;
import com.example.userservice.dto.UserDTO;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserCreateDTO validDto;
    private UserDTO expectedDto;

    @BeforeEach
    void setUp() {
        testUser = new User("Test", "test@example.com", 30);
        testUser.setId(1L);

        validDto = new UserCreateDTO("Test", "test@example.com", 30);

        expectedDto = new UserDTO(
                1L,
                "Test",
                "test@example.com",
                30,
                LocalDateTime.of(2023, 1, 1, 12, 0)
        );
    }

    @Test
    @DisplayName("should create user with valid data")
    void createUser_ValidData() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDTO result = userService.createUser(validDto);


        assertNotNull(result);
        assertEquals(expectedDto.id(), result.id());
        assertEquals(expectedDto.name(), result.name());
        assertEquals(expectedDto.email(), result.email());
        assertEquals(expectedDto.age(), result.age());
        assertEquals(expectedDto.createdAt(), result.createdAt());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("should throw exception with invalid email format")
    void createUser_InvalidEmailFormat() {

        UserCreateDTO invalidDto = new UserCreateDTO("Test", "invalid-email", 30);


        assertThrows(RuntimeException.class, () ->
                userService.createUser(invalidDto)
        );
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("should throw exception with invalid age")
    void createUser_InvalidAge() {

        UserCreateDTO invalidDto = new UserCreateDTO("Test", "test@example.com", -5);


        assertThrows(RuntimeException.class, () ->
                userService.createUser(invalidDto)
        );
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("should update user with valid data")
    void updateUser_ValidData() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserCreateDTO updateDto = new UserCreateDTO("Updated", "updated@example.com", 40);


        UserDTO result = userService.updateUser(1L, updateDto);


        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Updated", result.name());
        assertEquals("updated@example.com", result.email());
        assertEquals(40, result.age());
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("should throw exception when updating non-existent user")
    void updateUser_UserNotFound() {

        when(userRepository.findById(999L)).thenReturn(Optional.empty());


        assertThrows(RuntimeException.class, () ->
                userService.updateUser(999L, validDto)
        );
        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("should return all users")
    void getAllUsers() {

        User user1 = new User("User1", "user1@example.com", 25);
        User user2 = new User("User2", "user2@example.com", 35);
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));


        List<UserDTO> result = userService.getAllUsers();


        assertEquals(2, result.size());
        assertEquals("User1", result.get(0).name());
        assertEquals("User2", result.get(1).name());
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("should return empty list when no users")
    void getAllUsers_Empty() {

        when(userRepository.findAll()).thenReturn(List.of());


        List<UserDTO> result = userService.getAllUsers();


        assertTrue(result.isEmpty());
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("should get user by ID")
    void getUserById_Exists() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));


        Optional<UserDTO> result = userService.getUserById(1L);


        assertTrue(result.isPresent());
        assertEquals(expectedDto.id(), result.get().id());
        assertEquals(expectedDto.name(), result.get().name());
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("should return empty optional for non-existent user")
    void getUserById_NotExists() {

        when(userRepository.findById(999L)).thenReturn(Optional.empty());


        Optional<UserDTO> result = userService.getUserById(999L);


        assertFalse(result.isPresent());
        verify(userRepository).findById(999L);
    }

    @Test
    @DisplayName("should delete user when exists")
    void deleteUser_Exists() {

        when(userRepository.existsById(1L)).thenReturn(true);


        boolean result = userService.deleteUser(1L);


        assertTrue(result);
        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    @DisplayName("should not delete non-existent user")
    void deleteUser_NotExists() {
        when(userRepository.existsById(999L)).thenReturn(false);

        boolean result = userService.deleteUser(999L);

        assertFalse(result);
        verify(userRepository).existsById(999L);
        verify(userRepository, never()).deleteById(999L);
    }

    @Test
    @DisplayName("should throw exception for invalid ID in delete")
    void deleteUser_InvalidId() {
        assertThrows(IllegalArgumentException.class, () ->
                userService.deleteUser(null)
        );
        assertThrows(IllegalArgumentException.class, () ->
                userService.deleteUser(-1L)
        );
        verify(userRepository, never()).existsById(any());
        verify(userRepository, never()).deleteById(any());
    }
}