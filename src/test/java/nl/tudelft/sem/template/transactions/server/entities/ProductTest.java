package nl.tudelft.sem.template.transactions.server.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



class ProductTest {
    Product product;

    @BeforeEach
    void setup() {
        product = new Product();
    }

    @Test
    void getProductName() {
        product.setProductName("Cream");
        assertEquals("Cream", product.getProductName());
    }

    @Test
    void getUsername() {
        product.setUsername("Alice");
        assertEquals("Alice", product.getUsername());
    }

    @Test
    void getPrice() {
        product.setPrice(128.0f);
        assertEquals(128.0f, product.getPrice());
    }

    @Test
    void getTotalPortions() {
        product.setTotalPortions(12);
        assertEquals(12, product.getTotalPortions());
    }

    @Test
    void getExpired() {
        assertEquals(0, product.getExpired());
    }
}