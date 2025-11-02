package com.example.shop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.Map;
import com.example.shop.entity.*; import com.example.shop.repository.*; import com.example.shop.service.OrderService; import com.example.shop.util.TokenService;

@RestController @RequestMapping("/api/orders")
public class OrderController {
    private final TokenService tokenService; private final UserRepository userRepository; private final OrderRepository orderRepository; private final OrderService orderService;
    public OrderController(TokenService t, UserRepository u, OrderRepository o, OrderService s){ this.tokenService=t; this.userRepository=u; this.orderRepository=o; this.orderService=s; }
    private User auth(String token){
        if (token == null || token.isBlank()) throw new RuntimeException("Missing token");
        Long userId = tokenService.getUserIdForToken(token);
        if (userId == null) throw new RuntimeException("Invalid token");
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    }
    @GetMapping public List<Order> list(@RequestHeader("X-Auth-Token") String token){ User u = auth(token); return orderRepository.findByUserIdOrderByCreatedAtDesc(u.getId()); }
    @PostMapping public ResponseEntity<?> create(@RequestHeader("X-Auth-Token") String token){ User u = auth(token); Order order = orderService.createOrderFromCart(u); return ResponseEntity.ok(Map.of("id", order.getId(), "total", order.getTotal())); }
}
