package com.example.shop.service;

import com.example.shop.entity.User;
import com.example.shop.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(String email, String name, String password) {
        Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isPresent()) {
            throw new RuntimeException("Email already used");
        }
        User user = new User();
        user.setEmail(email);
        if (name == null || name.isBlank()) {
            int atIndex = email.indexOf("@");
            if (atIndex > 0) {
                user.setName(email.substring(0, atIndex));
            } else {
                user.setName(email);
            }
        } else {
            user.setName(name);
        }
        user.setPasswordHash(encoder.encode(password));
        user.setAdmin(false);
        return userRepository.save(user);
    }

    public Optional<User> login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        User user = userOpt.get();
        if (!encoder.matches(password, user.getPasswordHash())) {
            return Optional.empty();
        }
        return Optional.of(user);
    }
}
