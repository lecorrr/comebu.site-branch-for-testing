package com.example.shop.controller;

import com.example.shop.entity.Order;
import com.example.shop.entity.OrderItem;
import com.example.shop.entity.Product;
import com.example.shop.entity.User;
import com.example.shop.repository.OrderRepository;
import com.example.shop.repository.ProductRepository;
import com.example.shop.repository.UserRepository;
import com.example.shop.util.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public OrderController(OrderRepository orderRepository,
                           ProductRepository productRepository,
                           UserRepository userRepository,
                           TokenService tokenService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> body,
                                         @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        Long userId = getUserIdFromHeader(authorizationHeader);
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        Object itemsObj = body.get("items");
        if (!(itemsObj instanceof List<?> rawList)) {
            return ResponseEntity.badRequest().body(Map.of("error", "items must be a list"));
        }

        List<OrderItem> items = new ArrayList<>();
        for (Object o : rawList) {
            if (!(o instanceof Map)) {
                continue;
            }
            Map<?, ?> itemMap = (Map<?, ?>) o;
            Object productIdObj = itemMap.get("productId");
            Object quantityObj = itemMap.get("quantity");
            if (productIdObj == null || quantityObj == null) {
                continue;
            }
            Long productId;
            int quantity;
            try {
                productId = Long.parseLong(productIdObj.toString());
                quantity = Integer.parseInt(quantityObj.toString());
            } catch (NumberFormatException ex) {
                continue;
            }
            if (quantity <= 0) {
                continue;
            }
            Optional<Product> productOpt = productRepository.findById(productId);
            if (productOpt.isEmpty()) {
                continue;
            }
            Product product = productOpt.get();
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(quantity);
            orderItem.setPrice(product.getPrice());
            items.add(orderItem);
        }

        if (items.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No valid items"));
        }

        Order order = new Order();
        order.setUser(userOpt.get());
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("NEW");
        for (OrderItem item : items) {
            item.setOrder(order);
        }
        order.setItems(items);

        Order saved = orderRepository.save(order);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyOrders(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        Long userId = getUserIdFromHeader(authorizationHeader);
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        return ResponseEntity.ok(orderRepository.findByUserId(userId));
    }

    private Long getUserIdFromHeader(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authorizationHeader.substring("Bearer ".length()).trim();
        return tokenService.getUserIdForToken(token);
    }
}
