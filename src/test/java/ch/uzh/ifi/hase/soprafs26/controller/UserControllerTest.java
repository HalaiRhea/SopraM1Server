package ch.uzh.ifi.hase.soprafs26.controller;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;


import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs26.service.UserService;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserPutDTO;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setBio("test bio");
        user.setStatus(UserStatus.ONLINE);
        user.setCreationDate(Instant.now());

        List<User> allUsers = Collections.singletonList(user);
        given(userService.getUsers()).willReturn(allUsers);

        MockHttpServletRequestBuilder getRequest =
                get("/users").contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(user.getId().intValue())))
                .andExpect(jsonPath("$[0].username", is(user.getUsername())))
                .andExpect(jsonPath("$[0].bio", is(user.getBio())))
                .andExpect(jsonPath("$[0].status", is(user.getStatus().toString())));
    }

    @Test
    public void createUser_validInput_userCreated() throws Exception {
        User createdUser = new User();
        createdUser.setId(1L);
        createdUser.setUsername("testUsername");
        createdUser.setBio("test bio");
        createdUser.setStatus(UserStatus.ONLINE);
        createdUser.setCreationDate(Instant.now());

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("testUsername");
        userPostDTO.setPassword("testPassword");
        userPostDTO.setBio("test bio");

        given(userService.register(Mockito.any())).willReturn(createdUser);

        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userPostDTO));

        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(createdUser.getId().intValue())))
                .andExpect(jsonPath("$.username", is(createdUser.getUsername())))
                .andExpect(jsonPath("$.bio", is(createdUser.getBio())))
                .andExpect(jsonPath("$.status", is(createdUser.getStatus().toString())));
    }

    @Test
    public void getUserById_validId_userReturned() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setBio("bio");
        user.setStatus(UserStatus.ONLINE);
        user.setCreationDate(Instant.now());

        given(userService.getUserById(1L)).willReturn(user);

        mockMvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("testUser")))
                .andExpect(jsonPath("$.bio", is("bio")))
                .andExpect(jsonPath("$.status", is("ONLINE")));
    }

    @Test
    public void getUserById_userNotFound_returns404() throws Exception {
        given(userService.getUserById(99L))
                .willThrow(new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateUser_validInput_noContentReturned() throws Exception {
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setPassword("newPassword");

        MockHttpServletRequestBuilder putRequest =
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userPutDTO));

        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent());
    }

    @Test
    public void updateUser_userNotFound_returns404() throws Exception {
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setPassword("newPassword");

        Mockito.doThrow(new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"))
                .when(userService).updatePassword(Mockito.eq(99L), Mockito.any());

        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .put("/users/99")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userPutDTO)))
                .andExpect(status().isNotFound());
    }
}