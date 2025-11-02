package com.example.shop.config;

import org.springframework.stereotype.Component;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import com.example.shop.repository.ProductRepository;
import com.example.shop.entity.Product;

@Component
public class DataInitializer {
    private final ProductRepository productRepository;
    public DataInitializer(ProductRepository productRepository){ this.productRepository=productRepository; }
    @EventListener(ApplicationReadyEvent.class)
    public void init(){
        if (productRepository.count() == 0) {
            productRepository.save(new Product("Classic Burger","Juicy burger",5.99,100,"/images/burger-icon.png"));
            productRepository.save(new Product("Broccoli","Fresh broccoli",1.99,200,"/images/brocoli.png"));
            productRepository.save(new Product("Canvas Bag","Eco bag",8.99,50,"/images/bags.png"));
        }
    }
}
