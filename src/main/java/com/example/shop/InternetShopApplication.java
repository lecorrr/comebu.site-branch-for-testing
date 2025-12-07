package com.example.shop;

import com.example.shop.entity.Image;
import com.example.shop.entity.Product;
import com.example.shop.entity.Role;
import com.example.shop.entity.User;
import com.example.shop.repository.ImageRepository;
import com.example.shop.repository.ProductRepository;
import com.example.shop.repository.RoleRepository;
import com.example.shop.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

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
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }

    @Bean
    public CommandLineRunner initData(UserRepository userRepository,
                                      RoleRepository roleRepository,
                                      ImageRepository imageRepository,
                                      ProductRepository productRepository) {
        return args -> {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> {
                Role r = new Role();
                r.setName("ROLE_ADMIN");
                return roleRepository.save(r);
            });
            Role userRole = roleRepository.findByName("ROLE_USER").orElseGet(() -> {
                Role r = new Role();
                r.setName("ROLE_USER");
                return roleRepository.save(r);
            });

            userRepository.findByEmail("admim").orElseGet(() -> {
                User admin = new User();
                admin.setEmail("admim");
                admin.setName("Admin");
                admin.setAdmin(true);
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                admin.setPasswordHash(encoder.encode("password"));
                admin.getRoles().add(adminRole);
                return userRepository.save(admin);
            });

            Image neko = saveImageIfMissing(imageRepository, "neko-ark", "png", "/images/goods/neko-ark.png");
            Image pizza = saveImageIfMissing(imageRepository, "pizza", "png", "/images/goods/pizza.png");
            Image sushi = saveImageIfMissing(imageRepository, "sushi", "webp", "/images/goods/sushi.webp");

            if (productRepository.count() == 0) {
                Product p1 = new Product();
                p1.setName("Бургер з Неко-Арк");
                p1.setDescription("Соковитий бургер з котиком Неко-Арк");
                p1.setWeight("250 г");
                p1.setPrice(199.0);
                p1.setImageEntity(neko);

                Product p2 = new Product();
                p2.setName("Піца Маргарита");
                p2.setDescription("Класична піца з сиром моцарела");
                p2.setWeight("500 г");
                p2.setPrice(249.0);
                p2.setImageEntity(pizza);

                Product p3 = new Product();
                p3.setName("Сет суші");
                p3.setDescription("Набір з 24 шматочків суші");
                p3.setWeight("400 г");
                p3.setPrice(299.0);
                p3.setImageEntity(sushi);

                productRepository.saveAll(List.of(p1, p2, p3));
            }
        };
    }

    private Image saveImageIfMissing(ImageRepository repo, String name, String ext, String path) {
        return repo.findByName(name).orElseGet(() -> {
            Image img = new Image();
            img.setName(name);
            img.setExtension(ext);
            img.setPath(path);
            return repo.save(img);
        });
    }
}
