package dev.m4nd3l.chatting4ever.database.service;

import dev.m4nd3l.chatting4ever.database.model.User;
import dev.m4nd3l.chatting4ever.database.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) { this.userRepository = userRepository; }

    public User getUserByID(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional.orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
    }

    public User getUserByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional.orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }
}