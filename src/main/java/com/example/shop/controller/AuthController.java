package com.example.shop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;
import com.example.shop.entity.User;
import com.example.shop.service.AuthService;
import com.example.shop.repository.UserRepository;
import com.example.shop.util.TokenService;

@RestController @RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService; private final TokenService tokenService; private final UserRepository userRepository;
    public AuthController(AuthService a, TokenService t, UserRepository u){ this.authService=a; this.tokenService=t; this.userRepository=u; }

    @PostMapping(value="/register", consumes={"application/json","application/x-www-form-urlencoded"})
    public ResponseEntity<?> registerJsonOrForm(
            @RequestParam(required=false) String email,
            @RequestParam(required=false) String name,
            @RequestParam(required=false) String password,
            @RequestBody(required=false) Map<String,String> body) {

        if (body != null) {
            email = (email==null)? body.get("email") : email;
            name = (name==null)? body.get("name") : name;
            password = (password==null)? body.get("password") : password;
        }
        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error","email and password are required"));
        }
        User u = authService.register(email, (name != null && !name.isBlank()) ? name : email.split("@")[0], password);
        String token = tokenService.createTokenFor(u.getId());
        return ResponseEntity.ok(Map.of("token", token, "user", Map.of("id", u.getId(), "email", u.getEmail(), "name", u.getName())));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> body){
        String email = body.get("email"); String password = body.get("password");
        Optional<User> maybe = authService.login(email, password);
        if (maybe.isEmpty()) return ResponseEntity.status(401).body(Map.of("error","invalid credentials"));
        User u = maybe.get(); String token = tokenService.createTokenFor(u.getId());
        return ResponseEntity.ok(Map.of("token", token, "user", Map.of("id", u.getId(), "email", u.getEmail(), "name", u.getName())));
    }
}
