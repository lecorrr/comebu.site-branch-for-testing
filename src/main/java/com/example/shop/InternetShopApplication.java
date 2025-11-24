package com.example.shop;

import com.example.shop.entity.User;
import com.example.shop.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class InternetShopApplication {
    public static void main(String[] args) {
        SpringApplication.run(InternetShopApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }

    @Bean
    public CommandLineRunner initAdmin(UserRepository userRepository) {
        return args -> {
            userRepository.findByEmail("admin").orElseGet(() -> {
                User user = new User();
                user.setEmail("admin");
                user.setName("admin");
                user.setAdmin(true);
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                user.setPasswordHash(encoder.encode("password"));
                return userRepository.save(user);
            });
        };
    }
}
