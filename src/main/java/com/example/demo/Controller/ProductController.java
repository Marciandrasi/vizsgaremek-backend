package com.example.demo.Controller;

import com.example.demo.Dto.ProductDto;
import com.example.demo.Entity.Product;
import com.example.demo.Service.ProductService;
import com.example.demo.Service.StorageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final StorageService storageService;

    public ProductController(ProductService productService, StorageService storageService) {
        this.productService = productService;
        this.storageService = storageService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }


    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> createProduct(@ModelAttribute @Valid ProductDto dto,
                                                  @RequestPart(required = false) MultipartFile image) {
        if (image != null && !image.isEmpty()) {
            String filename = storageService.store(image);
            dto.setImageUrl("/uploads/" + filename);
        }
        Product product = productService.saveProduct(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id,
                                                  @ModelAttribute @Valid ProductDto dto,
                                                  @RequestPart(required = false) MultipartFile image) {
        if (image != null && !image.isEmpty()) {
            String filename = storageService.store(image);
            dto.setImageUrl("/uploads/" + filename);
        }
        Product product = productService.updateProduct(id, dto);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}