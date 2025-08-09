package com.org.product_catalog.service.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.org.product_catalog.entity.Product;
import com.org.product_catalog.repo.ProductRepo;
import com.org.product_catalog.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepo repo;

    public ProductServiceImpl(ProductRepo repo) {
        this.repo = repo;
    }

    @Override
    public List<Product> listAll() {
        log.info("Fetching all products");
        List<Product> products = repo.findAll();
        log.debug("Products found: {}", products.size());
        return products;
    }

    @Override
    public Optional<Product> getById(Long id) {
        log.info("Fetching product by ID: {}", id);
        Optional<Product> product = repo.findById(id);
        if (product.isPresent()) {
            log.debug("Product found: {}", product.get());
        } else {
            log.warn("No product found with ID: {}", id);
        }
        return product;
    }

    @Override
    public Product create(Product p) {
        log.info("Creating product: {}", p);
        Product savedProduct = repo.save(p);
        log.debug("Product created with ID: {}", savedProduct.getId());
        return savedProduct;
    }

    @Override
    public Optional<Product> update(Long id, Product p) {
        log.info("Updating product with ID: {}", id);
        return repo.findById(id).map(existing -> {
            log.debug("Existing product before update: {}", existing);
            existing.setName(p.getName());
            existing.setDescription(p.getDescription());
            existing.setPrice(p.getPrice());
            existing.setCategory(p.getCategory());
            Product updatedProduct = repo.save(existing);
            log.debug("Updated product: {}", updatedProduct);
            return updatedProduct;
        });
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting product with ID: {}", id);
        repo.deleteById(id);
        log.debug("Product deleted with ID: {}", id);
    }

    @Override
    public List<Product> search(String q) {
        log.info("Searching products by query: {}", q);
        List<Product> results = repo.searchByNameOrCategory(q);
        log.debug("Search results count: {}", results.size());
        return results;
    }

    @Override
    public List<Product> findByCategory(String category) {
        log.info("Fetching products by category: {}", category);
        List<Product> results = repo.findByCategoryIgnoreCase(category);
        log.debug("Products found in category '{}': {}", category, results.size());
        return results;
    }
}
