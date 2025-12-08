package com.example.shop.controller;

import com.example.shop.entity.Image;
import com.example.shop.repository.ImageRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/images")
@CrossOrigin
public class ImageController {

    private final ImageRepository imageRepository;

    public ImageController(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @GetMapping
    public List<Image> getAll() {
        return imageRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Image> getOne(@PathVariable Long id) {
        return imageRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Image> create(@RequestBody Image image) {
        Image saved = imageRepository.save(image);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Image> update(@PathVariable Long id,
                                        @RequestBody Image data) {
        return imageRepository.findById(id)
                .map(existing -> {
                    existing.setName(data.getName());
                    existing.setExtension(data.getExtension());
                    existing.setPath(data.getPath());
                    Image saved = imageRepository.save(existing);
                    return ResponseEntity.ok(saved);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (imageRepository.existsById(id)) {
            imageRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
