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
    void addProduct() {
        Product addedProduct = productController.addProduct(product.getProductName(),
                product.getPrice(), product.getTotalPortions(), "Mark");
        Assertions.assertEquals(product.getProductName(), addedProduct.getProductName());
        Assertions.assertEquals(product.getPrice(), addedProduct.getPrice());
        Assertions.assertEquals(product.getTotalPortions(), addedProduct.getTotalPortions());
    }

    @Test
    void getUserProducts() {
        productController.addProduct(product.getProductName(),
                product.getPrice(), product.getTotalPortions(), "Mark");
        productController.addProduct("Milk2", 14.0f, 18, "Bob");
        Assertions.assertEquals(2, productController.getAllProducts().size());
    }

    @Test
    void getAllProducts() {
        productController.addProduct(product.getProductName(),
                product.getPrice(), product.getTotalPortions(), "Mark");
        productController.addProduct("Milk2", 14.0f, 18, "Bob");
        Assertions.assertEquals(1, productController.getUserProducts("Mark").size());
    }

    @Test
    void deleteProduct() {
        Assertions.assertFalse(productController.deleteProduct(-128));
    }
}