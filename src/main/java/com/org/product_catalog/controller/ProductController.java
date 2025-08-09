package com.org.product_catalog.controller;

import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.org.product_catalog.entity.Product;
import com.org.product_catalog.metrics.EndpointMetricsService;
import com.org.product_catalog.model.ApiResponseModel;
import com.org.product_catalog.service.ProductService;

@RestController
@RequestMapping("/products")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final ProductService service;
    private final EndpointMetricsService metricsService;

    public ProductController(ProductService service, EndpointMetricsService metricsService) {
        this.metricsService = metricsService;
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponseModel<List<Product>>> all() {
        try {
            List<Product> products = service.listAll();
            metricsService.incrementSuccess("GET /products");
            log.info("Fetched all products: count={}", products.size());
            return ResponseEntity.ok(ApiResponseModel.success("Products retrieved successfully", products));
        } catch (Exception e) {
            metricsService.incrementFailure("GET /products");
            log.error("Error fetching products", e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseModel<Product>> getById(@PathVariable("id") Long id) {
        try {
            Product product = service.getById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
            metricsService.incrementSuccess("GET /products/{id}");
            log.info("Fetched product with id={}", id);
            return ResponseEntity.ok(ApiResponseModel.success("Product retrieved successfully", product));
        } catch (Exception e) {
            metricsService.incrementFailure("GET /products/{id}");
            log.error("Error fetching product id={}", id, e);
            throw e;
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponseModel<Product>> create(@RequestBody Product product) {
        try {
            Product created = service.create(product);
            metricsService.incrementSuccess("POST /products");
            log.info("Created product with id={}, name={}", created.getId(), created.getName());
            return ResponseEntity.created(URI.create("/products/" + created.getId()))
                    .body(ApiResponseModel.success("Product created successfully", created));
        } catch (Exception e) {
            metricsService.incrementFailure("POST /products");
            log.error("Error creating product: {}", product, e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseModel<Product>> update(@PathVariable("id") Long id, @RequestBody Product product) {
        try {
            return service.update(id, product)
                    .map(updated -> {
                        metricsService.incrementSuccess("PUT /products/{id}");
                        log.info("Updated product with id={}", id);
                        return ResponseEntity.ok(ApiResponseModel.success("Product updated successfully", updated));
                    })
                    .orElseGet(() -> {
                        metricsService.incrementFailure("PUT /products/{id}");
                        log.warn("Product not found for update: id={}", id);
                        return ResponseEntity.status(404)
                                .body(ApiResponseModel.error("Product not found"));
                    });
        } catch (Exception e) {
            metricsService.incrementFailure("PUT /products/{id}");
            log.error("Error updating product id={}", id, e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseModel<Void>> delete(@PathVariable("id") Long id) {
        try {
            service.delete(id);
            metricsService.incrementSuccess("DELETE /products/{id}");
            log.info("Deleted product with id={}", id);
            return ResponseEntity.ok(ApiResponseModel.success("Product deleted successfully", null));
        } catch (Exception e) {
            metricsService.incrementFailure("DELETE /products/{id}");
            log.error("Error deleting product id={}", id, e);
            throw e;
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponseModel<List<Product>>> search(@RequestParam(name = "query") String q) {
        try {
            List<Product> products = service.search(q);
            metricsService.incrementSuccess("GET /products/search");
            log.info("Search for query='{}' returned {} products", q, products.size());
            return ResponseEntity.ok(ApiResponseModel.success("Search completed", products));
        } catch (Exception e) {
            metricsService.incrementFailure("GET /products/search");
            log.error("Error searching products for query='{}'", q, e);
            throw e;
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponseModel<List<Product>>> byCategory(@PathVariable("category") String category) {
        try {
            List<Product> products = service.findByCategory(category);
            metricsService.incrementSuccess("GET /products/category/{category}");
            log.info("Fetched {} products for category='{}'", products.size(), category);
            return ResponseEntity.ok(ApiResponseModel.success("Products retrieved by category", products));
        } catch (Exception e) {
            metricsService.incrementFailure("GET /products/category/{category}");
            log.error("Error fetching products for category='{}'", category, e);
            throw e;
        }
    }
}
