package ch.uzh.ifi.hase.soprafs26.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUsername");
        testUser.setPassword("password");
        testUser.setBio("test bio");
    }

    @Test
    public void register_validInput_success() {
        when(userRepository.findByUsername("testUsername")).thenReturn(null);
        when(userRepository.saveAndFlush(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User createdUser = userService.register(testUser);

        verify(userRepository, times(1)).saveAndFlush(any(User.class));

        assertEquals("testUsername", createdUser.getUsername());
        assertEquals("password", createdUser.getPassword());
        assertEquals("test bio", createdUser.getBio());
        assertEquals(UserStatus.ONLINE, createdUser.getStatus());
        assertNotNull(createdUser.getCreationDate());
    }

    @Test
    public void register_duplicateUsername_throwsException() {
        when(userRepository.findByUsername("testUsername")).thenReturn(testUser);

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> userService.register(testUser));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(userRepository, never()).saveAndFlush(any());
    }
}