package com.example.shop.controller;

import com.example.shop.entity.User;
import com.example.shop.service.AuthService;
import com.example.shop.util.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final TokenService tokenService;

    public AuthController(AuthService authService, TokenService tokenService) {
        this.authService = authService;
        this.tokenService = tokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        String name = body.get("name");
        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "email and password are required"));
        }
        try {
            User user = authService.register(email, name, password);
            String token = tokenService.createTokenFor(user.getId());
            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "user", Map.of(
                            "id", user.getId(),
                            "email", user.getEmail(),
                            "name", user.getName(),
                            "admin", user.isAdmin()
                    )
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "email and password are required"));
        }
        Optional<User> userOpt = authService.login(email, password);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "invalid credentials"));
        }
        User user = userOpt.get();
        String token = tokenService.createTokenFor(user.getId());
        return ResponseEntity.ok(Map.of(
                "token", token,
                "user", Map.of(
                        "id", user.getId(),
                        "email", user.getEmail(),
                        "name", user.getName(),
                        "admin", user.isAdmin()
                )
        ));
    }
}
