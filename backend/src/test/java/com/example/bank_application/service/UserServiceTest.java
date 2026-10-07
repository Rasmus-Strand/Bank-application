package com.example.bank_application.service;


import com.example.bank_application.dto.users.UserRequest;
import com.example.bank_application.model.User;
import com.example.bank_application.model.UserRole;
import com.example.bank_application.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_shouldCreateUser() {

    }

    @Test
    void findUserById() {
        UUID id = UUID.randomUUID();

        User user = new User(
                "testuser",
                "hashedpassword",
                UserRole.USER
        );

        when(userRepository.findById(id))
                .thenReturn(Optional.of(user));

        Optional<User> result =
                userService.findUserById(id);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());

        verify(userRepository).findById(id);
    }

    @Test
    void findUserByName(){
        String name = "testuser";
        User user = new User(
                "testuser",
                "hashedpassword",
                UserRole.USER
        );

        when(userRepository.findByUsername(name))
                .thenReturn(Optional.of(user));

        Optional<User> result =
                userService.findByUsername(name);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());

        verify(userRepository).findByUsername(name);
    }

    @Test
    void findAllUsers() {
        User user1 = new User(
                "user1",
                "hashedpassword1",
                UserRole.USER
        );

        User user2 = new User(
                "user2",
                "hashedpassword2",
                UserRole.USER
        );

        List<User> users = List.of(user1, user2);
        when(userRepository.findAll())
                .thenReturn(users);
        List<User> result =
                userService.findAllUsers();

        assertEquals(users, result);

        verify(userRepository).findAll();
    }

    @Test
    void createUser() {
        UserRequest request = new UserRequest(
                "testuser",
                "password123"
        );
        when(passwordEncoder.encode("password123"))
                .thenReturn("hashedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.createUser(request);

        assertEquals("testuser", result.getUsername());
        assertEquals("hashedPassword", result.getPassword());
        assertEquals(UserRole.USER, result.getUserRole());

        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));

    }
}