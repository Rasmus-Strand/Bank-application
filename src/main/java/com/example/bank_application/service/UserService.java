package com.example.bank_application.service;

import com.example.bank_application.dto.UserRequest;
import com.example.bank_application.model.User;
import com.example.bank_application.model.UserRole;
import com.example.bank_application.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User createUser(UserRequest request){

        String hash = BCrypt.hashpw(
                request.getPassword(),
                BCrypt.gensalt()
        );

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
