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

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public User register(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setName(email);
        user.setAdmin(false);
        user.setPasswordHash(encoder.encode(password));
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
        return userOpt;
    }
}
