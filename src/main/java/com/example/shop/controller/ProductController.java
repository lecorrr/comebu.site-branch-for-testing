package com.example.shop.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.shop.entity.Product;
import com.example.shop.repository.ProductRepository;

@RestController @RequestMapping("/api/products")
public class ProductController {
    private final ProductRepository repo;
    public ProductController(ProductRepository r){ this.repo = r; }
    @GetMapping public List<Product> list(){ return repo.findAll(); }
    @GetMapping("/{id}") public Product get(@PathVariable Long id){ return repo.findById(id).orElseThrow(() -> new RuntimeException("Product not found")); }
}
