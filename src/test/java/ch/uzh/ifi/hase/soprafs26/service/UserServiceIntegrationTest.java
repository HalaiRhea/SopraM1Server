package ch.uzh.ifi.hase.soprafs26.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest
public class UserServiceIntegrationTest {

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
    }

    @Test
    public void register_validInputs_success() {
        assertNull(userRepository.findByUsername("testUsername"));

        User user = new User();
        user.setUsername("testUsername");
        user.setPassword("password");
        user.setBio("test bio");

        User createdUser = userService.register(user);

        User persistedUser = userRepository.findByUsername("testUsername");

        assertNotNull(persistedUser);
        assertEquals(createdUser.getId(), persistedUser.getId());
        assertEquals("testUsername", persistedUser.getUsername());
        assertEquals("password", persistedUser.getPassword());
        assertEquals("test bio", persistedUser.getBio());
        assertEquals(UserStatus.ONLINE, persistedUser.getStatus());
        assertNotNull(persistedUser.getCreationDate());
    }

    @Test
    public void register_duplicateUsername_throwsException() {
        User user1 = new User();
        user1.setUsername("testUsername");
        user1.setPassword("password");
        user1.setBio("bio");

        userService.register(user1);

        User user2 = new User();
        user2.setUsername("testUsername");
        user2.setPassword("otherPassword");
        user2.setBio("other bio");

        assertThrows(ResponseStatusException.class, () -> userService.register(user2));
    }
}