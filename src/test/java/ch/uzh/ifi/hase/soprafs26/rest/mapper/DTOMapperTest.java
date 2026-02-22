package ch.uzh.ifi.hase.soprafs26.rest.mapper;

import org.junit.jupiter.api.Test;

import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserPutDTO;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation works.
 */
public class DTOMapperTest {

    @Test
    public void testConvertUserPostDTOtoEntity_success() {
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("testUsername");
        userPostDTO.setPassword("testPassword");
        userPostDTO.setBio("test bio");

        User user = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        assertEquals("testUsername", user.getUsername());
        assertEquals("testPassword", user.getPassword());
        assertEquals("test bio", user.getBio());
    }

    @Test
    public void testConvertEntityToUserGetDTO_success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setBio("test bio");
        user.setStatus(UserStatus.ONLINE);
        user.setCreationDate(Instant.now());

        UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

        assertEquals(user.getId(), userGetDTO.getId());
        assertEquals(user.getUsername(), userGetDTO.getUsername());
        assertEquals(user.getBio(), userGetDTO.getBio());
        assertEquals(user.getStatus(), userGetDTO.getStatus());
        assertEquals(user.getCreationDate(), userGetDTO.getCreationDate());
    }

    @Test
    public void testConvertUserPutDTOtoEntity_success() {
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setPassword("newPassword");

        User user = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);

        assertEquals("newPassword", user.getPassword());
    }
}