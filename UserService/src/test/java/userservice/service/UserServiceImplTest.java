package userservice.service;

import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import userservice.dao.UserDAO;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = new User("Test", "test@example.com", 30);
        validUser.setId(1L);
        validUser.setCreatedAt(java.time.LocalDateTime.now());
    }

    @Test
    @DisplayName("should create user with valid data")
    void createUser_ValidData() {

        when(userDAO.save(any(User.class))).thenReturn(validUser);

        User result = userService.createUser("Test", "test@example.com", 30);

        assertNotNull(result);
        assertEquals("Test", result.getName());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(30, result.getAge());
        verify(userDAO).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertEquals("Test", capturedUser.getName());
        assertEquals("test@example.com", capturedUser.getEmail());
        assertEquals(30, capturedUser.getAge());
    }

    @Test
    @DisplayName("should throw exception with invalid email")
    void createUser_InvalidEmail() {
        assertThrows(IllegalArgumentException.class, () ->
                userService.createUser("Test", "invalid-email", 30)
        );
        verify(userDAO, never()).save(any());
    }

    @Test
    @DisplayName("should throw exception with invalid age")
    void createUser_InvalidAge() {
        assertThrows(IllegalArgumentException.class, () ->
                userService.createUser("Test", "test@example.com", -5)
        );
        verify(userDAO, never()).save(any());
    }

    @Test
    @DisplayName("should update user when all fields provided")
    void updateUser_AllFields() {
        when(userDAO.findById(1L)).thenReturn(Optional.of(validUser));
        when(userDAO.update(any(User.class))).thenReturn(true);

        boolean result = userService.updateUser(1L, "New", "new@example.com", 40);

        assertTrue(result);
        verify(userDAO).update(userCaptor.capture());
        User updatedUser = userCaptor.getValue();
        assertEquals("New", updatedUser.getName());
        assertEquals("new@example.com", updatedUser.getEmail());
        assertEquals(40, updatedUser.getAge());
    }

    @Test
    @DisplayName("should not update name when empty input provided")
    void updateUser_EmptyName() {
        when(userDAO.findById(1L)).thenReturn(Optional.of(validUser));
        when(userDAO.update(any(User.class))).thenReturn(true);

        boolean result = userService.updateUser(1L, "", "new@example.com", 40);

        assertTrue(result);
        verify(userDAO).update(userCaptor.capture());
        User updatedUser = userCaptor.getValue();
        assertEquals("Test", updatedUser.getName());
        assertEquals("new@example.com", updatedUser.getEmail());
        assertEquals(40, updatedUser.getAge());
    }

    @Test
    @DisplayName("should return all users")
    void getAllUsers() {
        User user1 = new User("User1", "user1@example.com", 25);
        User user2 = new User("User2", "user2@example.com", 35);
        List<User> mockUsers = List.of(user1, user2);

        when(userDAO.findAll()).thenReturn(mockUsers);

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("User1", result.get(0).getName());
        assertEquals("User2", result.get(1).getName());
        verify(userDAO).findAll();
    }

    @Test
    @DisplayName("should return empty list when no users exist")
    void getAllUsers_Empty() {
        when(userDAO.findAll()).thenReturn(List.of());

        List<User> result = userService.getAllUsers();

        assertTrue(result.isEmpty());
        verify(userDAO).findAll();
    }

    @Test
    @DisplayName("should delete user when exists")
    void deleteUser_Exists() {
        when(userDAO.deleteById(1L)).thenReturn(true);

        boolean result = userService.deleteUser(1L);

        assertTrue(result);
        verify(userDAO).deleteById(1L);
    }

    @Test
    @DisplayName("should not delete user when not exists")
    void deleteUser_NotExists() {
        when(userDAO.deleteById(999L)).thenReturn(false);

        boolean result = userService.deleteUser(999L);

        assertFalse(result);
        verify(userDAO).deleteById(999L);
    }

    @Test
    @DisplayName("should throw exception when deleting with invalid ID")
    void deleteUser_InvalidId() {
        assertThrows(IllegalArgumentException.class, () ->
                userService.deleteUser(null)
        );
        assertThrows(IllegalArgumentException.class, () ->
                userService.deleteUser(-1L)
        );
        verify(userDAO, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("should throw exception when updating with invalid ID")
    void updateUser_InvalidId() {
        assertThrows(IllegalArgumentException.class, () ->
                userService.updateUser(null, "New", "new@example.com", 40)
        );
        assertThrows(IllegalArgumentException.class, () ->
                userService.updateUser(-1L, "New", "new@example.com", 40)
        );
        verify(userDAO, never()).findById(anyLong());
        verify(userDAO, never()).update(any());
    }

    @Test
    @DisplayName("should not update user when not found")
    void updateUser_NotFound() {
        when(userDAO.findById(999L)).thenReturn(Optional.empty());

        boolean result = userService.updateUser(999L, "New", "new@example.com", 40);

        assertFalse(result);
        verify(userDAO).findById(999L);
        verify(userDAO, never()).update(any());
    }
}