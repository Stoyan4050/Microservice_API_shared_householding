package nl.tudelft.sem.transactions.controllers;

import nl.tudelft.sem.transactions.entities.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("PMD")
class ProductControllerTest {
    private Product product;
    private ProductController productController;

    @BeforeEach
    void setup() {
        product = new Product("Milk", 14.0f, 12, "Chris");
        productController = new ProductController();
    }

    @Test
    void deleteProduct() {
        Assertions.assertFalse(productController.deleteProduct(-128));
    }

    @Test
    void editProduct() {
        productController.editProduct(product);
        Assertions.assertEquals("Milk", this.product.getProductName());
    }
}