package com.org.product_catalog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.product_catalog.entity.Product;
import com.org.product_catalog.metrics.EndpointMetricsService;
import com.org.product_catalog.model.ApiResponseModel;
import com.org.product_catalog.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private EndpointMetricsService metricsService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = new Product();
        product1.setId(1L);
        product1.setName("Product A");
        product1.setCategory("Category1");
        product1.setPrice(10.0);

        product2 = new Product();
        product2.setId(2L);
        product2.setName("Product B");
        product2.setCategory("Category2");
        product2.setPrice(20.0);
    }

    @Test
    void testGetAllProducts() throws Exception {
        List<Product> products = Arrays.asList(product1, product2);
        Mockito.when(productService.listAll()).thenReturn(products);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].name", is("Product A")));
    }

    @Test
    void testGetProductById() throws Exception {
        Mockito.when(productService.getById(1L)).thenReturn(Optional.of(product1));

        mockMvc.perform(get("/products/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.name", is("Product A")));
    }

    @Test
    void testCreateProduct() throws Exception {
        Mockito.when(productService.create(any(Product.class))).thenReturn(product1);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product1)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/products/1")))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.name", is("Product A")));
    }

    @Test
    void testUpdateProduct() throws Exception {
        Mockito.when(productService.update(eq(1L), any(Product.class))).thenReturn(Optional.of(product1));

        mockMvc.perform(put("/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.name", is("Product A")));
    }

    @Test
    void testUpdateProductNotFound() throws Exception {
        Mockito.when(productService.update(eq(1L), any(Product.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product1)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Product not found")));
    }

    @Test
    void testDeleteProduct() throws Exception {
        mockMvc.perform(delete("/products/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Product deleted successfully")));
    }

    @Test
    void testSearchProducts() throws Exception {
        Mockito.when(productService.search("Prod")).thenReturn(Arrays.asList(product1));

        mockMvc.perform(get("/products/search").param("query", "Prod"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data[0].name", is("Product A")));
    }

    @Test
    void testGetProductsByCategory() throws Exception {
        Mockito.when(productService.findByCategory("Category1")).thenReturn(Arrays.asList(product1));

        mockMvc.perform(get("/products/category/{category}", "Category1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data[0].category", is("Category1")));
    }

    @Test
    void testCheckForCICD() throws Exception {
        mockMvc.perform(get("/products/cicd"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is("Your Service has been deployed")));
    }
}
