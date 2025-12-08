package com.example.shop.service;

import com.example.shop.dto.ProductRequest;
import com.example.shop.entity.Image;
import com.example.shop.entity.Product;
import com.example.shop.repository.ImageRepository;
import com.example.shop.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;

    public ProductService(ProductRepository productRepository,
                          ImageRepository imageRepository) {
        this.productRepository = productRepository;
        this.imageRepository = imageRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElseThrow();
    }

    /**
     * Legacy save method, kept for compatibility with any existing usages.
     * Prefer using create(ProductRequest) instead.
     */
    public Product save(Product product) {
        return productRepository.save(product);
    }

    public Product create(ProductRequest request) {
        Product product = new Product();
        applyRequestToProduct(product, request);
        return productRepository.save(product);
    }

    public Product update(Long id, ProductRequest request) {
        Product existing = productRepository.findById(id).orElseThrow();
        applyRequestToProduct(existing, request);
        return productRepository.save(existing);
    }

    private void applyRequestToProduct(Product product, ProductRequest request) {
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setWeight(request.getWeight());

        String rawImageName = request.getImageName();
        String imageName = rawImageName == null ? null : rawImageName.trim();

        if (imageName != null && !imageName.isEmpty()) {
            final String finalImageName = imageName;
            Image image = imageRepository.findByName(finalImageName)
                    .orElseGet(() -> {
                        Image img = new Image();
                        img.setName(finalImageName);
                        // By default, assume PNG in /images/goods/
                        img.setPath("/images/goods/" + finalImageName + ".png");
                        return imageRepository.save(img);
                    });
            product.setImageEntity(image);
        } else {
            product.setImageEntity(null);
        }
    }

    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }
}
