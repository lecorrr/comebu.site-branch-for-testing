package com.example.shop.controller;

import com.example.shop.entity.Product;
import com.example.shop.entity.User;
import com.example.shop.repository.ProductRepository;
import com.example.shop.repository.UserRepository;
import com.example.shop.util.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductRepository productRepository;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    public ProductController(ProductRepository productRepository, TokenService tokenService, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product.get());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Product product, @RequestHeader(value = "Authorization", required = false) String authorization) {
        if (!isAdmin(authorization)) {
            return ResponseEntity.status(403).body(Map.of("error", "forbidden"));
        }
        product.setId(null);
        Product saved = productRepository.save(product);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Product body, @RequestHeader(value = "Authorization", required = false) String authorization) {
        if (!isAdmin(authorization)) {
            return ResponseEntity.status(403).body(Map.of("error", "forbidden"));
        }
        Optional<Product> existingOpt = productRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Product existing = existingOpt.get();
        existing.setName(body.getName());
        existing.setDescription(body.getDescription());
        existing.setWeight(body.getWeight());
        existing.setPrice(body.getPrice());
        existing.setImage(body.getImage());
        Product saved = productRepository.save(existing);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String authorization) {
        if (!isAdmin(authorization)) {
            return ResponseEntity.status(403).body(Map.of("error", "forbidden"));
        }
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("status", "deleted"));
    }

    private boolean isAdmin(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return false;
        }
        String token = authorizationHeader.substring(7);
        Long userId = tokenService.getUserIdForToken(token);
        if (userId == null) {
            return false;
        }
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return false;
        }
        User user = userOpt.get();
        return user.isAdmin();
    }
}
