package com.org.product_catalog.repo;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.org.product_catalog.entity.Product;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {

	List<Product> findByCategoryIgnoreCase(String category);

    @Query("select p from Product p where lower(p.name) like lower(concat('%', :q, '%')) or lower(p.category) like lower(concat('%', :q, '%'))")
    List<Product> searchByNameOrCategory(@Param("q") String q);

}
