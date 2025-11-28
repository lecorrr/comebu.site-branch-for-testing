package com.example.shop.controller;

import com.example.shop.entity.Image;
import com.example.shop.entity.Product;
import com.example.shop.entity.User;
import com.example.shop.repository.ImageRepository;
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
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final ImageRepository imageRepository;

    public ProductController(ProductRepository productRepository,
                             UserRepository userRepository,
                             TokenService tokenService,
                             ImageRepository imageRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.imageRepository = imageRepository;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
        Optional<Product> product = productRepository.findById(id);
        return product.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Map<String, Object> body,
                                           @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        if (!isAdmin(authorizationHeader)) {
            return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
        }

        Product product = new Product();
        updateProductFromBody(product, body);
        Product saved = productRepository.save(product);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id,
                                           @RequestBody Map<String, Object> body,
                                           @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        if (!isAdmin(authorizationHeader)) {
            return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
        }
        Optional<Product> existingOpt = productRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Product product = existingOpt.get();
        updateProductFromBody(product, body);
        Product saved = productRepository.save(product);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id,
                                           @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        if (!isAdmin(authorizationHeader)) {
            return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
        }
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private boolean isAdmin(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return false;
        }
        String token = authorizationHeader.substring("Bearer ".length()).trim();
        Long userId = tokenService.getUserIdForToken(token);
        if (userId == null) {
            return false;
        }
        Optional<User> userOpt = userRepository.findById(userId);
        return userOpt.map(User::isAdmin).orElse(false);
    }

    private void updateProductFromBody(Product product, Map<String, Object> body) {
        Object nameObj = body.get("name");
        Object descriptionObj = body.get("description");
        Object weightObj = body.get("weight");
        Object priceObj = body.get("price");

        if (nameObj != null) {
            product.setName(nameObj.toString());
        }
        if (descriptionObj != null) {
            product.setDescription(descriptionObj.toString());
        }
        if (weightObj != null) {
            product.setWeight(weightObj.toString());
        }
        if (priceObj != null) {
            if (priceObj instanceof Number) {
                product.setPrice(((Number) priceObj).doubleValue());
            } else {
                try {
                    product.setPrice(Double.parseDouble(priceObj.toString()));
                } catch (NumberFormatException e) {
                    product.setPrice(0.0);
                }
            }
        }

        Object imageNameObj = body.get("imageName");
        if (imageNameObj == null) {
            imageNameObj = body.get("image");
        }
        if (imageNameObj != null) {
            String imageName = imageNameObj.toString().trim();
            if (!imageName.isEmpty()) {
                Optional<Image> imageOpt = imageRepository.findByName(imageName);
                product.setImageEntity(imageOpt.orElse(null));
            } else {
                product.setImageEntity(null);
            }
        }
    }
}
