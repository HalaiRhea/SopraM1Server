package ch.uzh.ifi.hase.soprafs26.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class UserRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void findByUsername_success() {
        User user = new User();
        user.setUsername("testUsername");
        user.setPassword("testPassword");
        user.setBio("test bio");
        user.setStatus(UserStatus.ONLINE);
        user.setCreationDate(Instant.now());

        entityManager.persist(user);
        entityManager.flush();

        User found = userRepository.findByUsername("testUsername");

        assertNotNull(found);
        assertNotNull(found.getId());

        assertEquals(user.getUsername(), found.getUsername());
        assertEquals(user.getPassword(), found.getPassword());
        assertEquals(user.getBio(), found.getBio());
        assertEquals(user.getStatus(), found.getStatus());
        assertEquals(user.getCreationDate(), found.getCreationDate());
    }
}
