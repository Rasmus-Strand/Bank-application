package com.example.bank_application.controller;

import com.example.bank_application.dto.users.UserRequest;
import com.example.bank_application.dto.users.UserResponse;
import com.example.bank_application.model.User;
import com.example.bank_application.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers(){
        List<User> users = userService.findAllUsers();

        List<UserResponse> responses = users.stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getUserRole()
                ))
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id){
        Optional<User> user = userService.findUserById(id);

        if (user.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        User foundUser = user.get();

        UserResponse response = new UserResponse(
                foundUser.getId(),
                foundUser.getUsername(),
                foundUser.getUserRole()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<UserResponse> addUser(@Valid @RequestBody UserRequest request){
        User savedUser = userService.createUser(request);

        UserResponse response = new UserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getUserRole()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
