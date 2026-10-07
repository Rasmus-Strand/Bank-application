package com.example.bank_application.controller;

import com.example.bank_application.dto.users.UserRequest;
import com.example.bank_application.dto.users.UserResponse;
import com.example.bank_application.model.User;
import com.example.bank_application.model.UserRole;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bank_application.service.UserService;
import com.example.bank_application.service.security.JwtAuthenticationFilter;
import com.example.bank_application.service.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void createUser_shouldReturnCreatedUser() throws Exception {

        UUID userId = UUID.randomUUID();

        User user = new User(
                "testuser",
                "hashedPassword",
                UserRole.USER
        );

        // JPA skapar normalt ID:t.
        // I ett controller-test kör vi inte databasen.
        org.springframework.test.util.ReflectionTestUtils.setField(
                user,
                "id",
                userId
        );

        when(userService.createUser(any(UserRequest.class)))
                .thenReturn(user);

        mockMvc.perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                        "username": "testuser",
                                        "password": "password123"
                                    }
                                    """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.userRole").value("USER"));

        verify(userService)
                .createUser(any(UserRequest.class));
    }

    @Test
    void createUser_shouldReturnBadRequestWhenFieldsAreBlank() throws Exception {

        mockMvc.perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "username": "",
                                    "password": ""
                                }
                                """)
                )
                .andExpect(status().isBadRequest());

        verify(userService, never())
                .createUser(any(UserRequest.class));
    }

    @Test
    void createUser_shouldReturnBadRequestWhenFieldsAreMissing() throws Exception {

        mockMvc.perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "username": "testuser"
                                }
                                """)
                )
                .andExpect(status().isBadRequest());

        verify(userService, never())
                .createUser(any(UserRequest.class));
    }

    @Test
    void getUsers_shouldReturnAllUsers() throws Exception {

        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        User user1 = new User(
                "user1",
                "hashedPassword",
                UserRole.USER
        );

        User user2 = new User(
                "user2",
                "hashedPassword",
                UserRole.USER
        );

        org.springframework.test.util.ReflectionTestUtils.setField(
                user1,
                "id",
                userId1
        );

        org.springframework.test.util.ReflectionTestUtils.setField(
                user2,
                "id",
                userId2
        );

        when(userService.findAllUsers())
                .thenReturn(List.of(user1, user2));

        mockMvc.perform(
                        get("/api/users")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(userId1.toString()))
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[0].userRole").value("USER"))
                .andExpect(jsonPath("$[1].id").value(userId2.toString()))
                .andExpect(jsonPath("$[1].username").value("user2"))
                .andExpect(jsonPath("$[1].userRole").value("USER"));

        verify(userService)
                .findAllUsers();
    }

    @Test
    void getUserById_shouldReturnUser() throws Exception {

        UUID userId = UUID.randomUUID();

        User user = new User(
                "testuser",
                "hashedPassword",
                UserRole.USER
        );

        org.springframework.test.util.ReflectionTestUtils.setField(
                user,
                "id",
                userId
        );

        when(userService.findUserById(userId))
                .thenReturn(java.util.Optional.of(user));

        mockMvc.perform(
                        get("/api/users/" + userId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.userRole").value("USER"));

        verify(userService)
                .findUserById(userId);
    }

    @Test
    void getUserById_shouldReturnNotFoundWhenUserDoesNotExist()
            throws Exception {

        UUID userId = UUID.randomUUID();

        when(userService.findUserById(userId))
                .thenReturn(java.util.Optional.empty());

        mockMvc.perform(
                        get("/api/users/" + userId)
                )
                .andExpect(status().isNotFound());

        verify(userService)
                .findUserById(userId);
    }
}