package nl.tudelft.sem.transactions.controllers;

import nl.tudelft.sem.transactions.entities.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

@SuppressWarnings("PMD")
class ProductControllerTest {
    private Product product;
    private ProductController productController;

    @BeforeEach
    void setup() {
        product = new Product("Milk", 14.0f, 12, "Chris");
        productController = new ProductController();
    }
    
}