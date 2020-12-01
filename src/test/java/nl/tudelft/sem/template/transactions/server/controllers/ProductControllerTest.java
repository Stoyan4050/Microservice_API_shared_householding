package nl.tudelft.sem.template.transactions.server.controllers;

import nl.tudelft.sem.template.transactions.server.entities.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("PMD")
class ProductControllerTest {
    private Product product;
    private ProductController productController;

    @BeforeEach
    void setup() {
        product = new Product("Milk", 14.0f, 12);
        productController = new ProductController();
    }

    @Test
    void deleteProduct() {
        Assertions.assertFalse(productController.deleteProduct(-128));
    }
}