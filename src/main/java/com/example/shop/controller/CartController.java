package com.example.shop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.Map;
import com.example.shop.entity.*; import com.example.shop.repository.*; import com.example.shop.util.TokenService;

@RestController @RequestMapping("/api/cart")
public class CartController {
    private final TokenService tokenService; private final UserRepository userRepository; private final ProductRepository productRepository; private final CartItemRepository cartItemRepository;
    public CartController(TokenService t, UserRepository u, ProductRepository p, CartItemRepository c){ this.tokenService=t; this.userRepository=u; this.productRepository=p; this.cartItemRepository=c; }
    private User auth(String token){
        if (token == null || token.isBlank()) throw new RuntimeException("Missing token");
        Long userId = tokenService.getUserIdForToken(token);
        if (userId == null) throw new RuntimeException("Invalid token");
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    }
    @GetMapping public List<CartItem> list(@RequestHeader("X-Auth-Token") String token){ User u = auth(token); return cartItemRepository.findByUserId(u.getId()); }
    @PostMapping public ResponseEntity<?> add(@RequestHeader("X-Auth-Token") String token, @RequestBody Map<String,Object> body){
        User u = auth(token); Long productId = ((Number)body.get("productId")).longValue(); int qty = ((Number)body.getOrDefault("qty",1)).intValue();
        Product p = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        CartItem item = cartItemRepository.findByUserAndProduct(u,p).orElse(new CartItem(u,p,0)); item.setQty(item.getQty()+qty); cartItemRepository.save(item);
        return ResponseEntity.ok(Map.of("ok", true));
    }
    @PutMapping("/{itemId}") public ResponseEntity<?> update(@RequestHeader("X-Auth-Token") String token, @PathVariable Long itemId, @RequestBody Map<String,Object> body){
        User u = auth(token); CartItem item = cartItemRepository.findById(itemId).orElseThrow(() -> new RuntimeException("Item not found"));
        if (!item.getUser().getId().equals(u.getId())) throw new RuntimeException("Forbidden"); int qty = ((Number)body.get("qty")).intValue();
        item.setQty(qty); cartItemRepository.save(item); return ResponseEntity.ok(Map.of("ok", true));
    }
    @DeleteMapping("/{itemId}") public ResponseEntity<?> remove(@RequestHeader("X-Auth-Token") String token, @PathVariable Long itemId){
        User u = auth(token); CartItem item = cartItemRepository.findById(itemId).orElseThrow(() -> new RuntimeException("Item not found"));
        if (!item.getUser().getId().equals(u.getId())) throw new RuntimeException("Forbidden"); cartItemRepository.delete(item); return ResponseEntity.ok(Map.of("ok", true));
    }
}
