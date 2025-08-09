package com.org.product_catalog.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.org.product_catalog.entity.Product;


public interface ProductService {

	List<Product> listAll();

	Optional<Product> getById(Long id);

	Product create(Product product);

	Optional<Product>  update(Long id, Product product);

	void delete(Long id);

	List<Product> search(String q);

	List<Product> findByCategory(String category);

}
