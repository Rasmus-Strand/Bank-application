package com.example.bank_application.service;

import com.example.bank_application.config.SecurityConfig;
import com.example.bank_application.dto.users.UserRequest;
import com.example.bank_application.model.User;
import com.example.bank_application.model.UserRole;
import com.example.bank_application.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(UserRequest request){

        String hash = passwordEncoder.encode(request.getPassword());

        User hashedUser = new User(
                request.getUsername(),
                hash,
                UserRole.USER
        );
        return userRepository.save(hashedUser);
    }

    public Optional<User> findUserById(UUID id){
        return userRepository.findById(id);
    }
    public Optional<User> findByUsername(String name){
        return userRepository.findByUsername(name);
    }
    public List<User> findAllUsers(){
        return userRepository.findAll();
    }
}
