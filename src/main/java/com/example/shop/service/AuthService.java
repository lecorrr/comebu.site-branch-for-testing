package com.example.shop.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import com.example.shop.entity.User;
import com.example.shop.repository.UserRepository;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    public AuthService(UserRepository userRepository){ this.userRepository = userRepository; }

    @Transactional
    public User register(String email, String name, String password) {
        userRepository.findByEmail(email).ifPresent(u -> { throw new RuntimeException("Email already used"); });
        User u = new User();
        u.setEmail(email);
        u.setName(name);
        u.setPasswordHash(encoder.encode(password));
        return userRepository.save(u);
    }

    public Optional<User> login(String email, String password){
        return userRepository.findByEmail(email).filter(u -> encoder.matches(password, u.getPasswordHash()));
    }
}
