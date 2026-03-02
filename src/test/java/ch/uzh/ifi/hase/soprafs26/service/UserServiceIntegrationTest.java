package ch.uzh.ifi.hase.soprafs26.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserPutDTO;

import java.time.Instant;
import java.util.Optional;

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

    private User createAndPersistUser() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("password");
        user.setBio("bio");

        return userService.register(user);
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

    @Test
    public void getUserById_validInputs_success() {
        User createdUser = createAndPersistUser();

        User fetchedUser = userService.getUserById(createdUser.getId());

        assertNotNull(fetchedUser);
        assertEquals(createdUser.getId(), fetchedUser.getId());
        assertEquals("testUser", fetchedUser.getUsername());
        assertEquals("password", fetchedUser.getPassword());
        assertEquals("bio", fetchedUser.getBio());
    }

    @Test
    public void getUserById_userNotFound_throwsException() {
        Long nonExistentId = 999L;

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class,
                        () -> userService.getUserById(nonExistentId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("999"));
    }

    @Test
    public void updatePassword_validInputs_success() {
        User createdUser = createAndPersistUser();

        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setPassword("newPassword");

        userService.updatePassword(createdUser.getId(), userPutDTO);

        User updatedUser = userRepository.findById(createdUser.getId()).orElseThrow();

        assertEquals("newPassword", updatedUser.getPassword());
    }

    @Test
    public void updatePassword_userNotFound_throwsException() {
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setPassword("newPassword");

        Long nonExistentId = 999L;

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class,
                        () -> userService.updatePassword(nonExistentId, userPutDTO));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
}