package nl.tudelft.sem.transactions.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("PMD")
class ProductTest {
    private Product product;
    private Transactions t1;
    private Transactions t2;

    @BeforeEach
    void setup() {

        product = new Product();

        t1 = new Transactions();
        t1.setProduct(product);
        t1.setPortions_consumed(4);
        t1.setUsername("Alice");

        t2 = new Transactions();
        t2.setProduct(product);
        t2.setPortions_consumed(1);
        t2.setUsername("Alice");

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

    @Test
    void getPortionsLeft() {
        product.setPortionsLeft(5);
        assertEquals(5, product.getPortionsLeft());
    }

    @Test
    void addTransaction() {
        this.product.addTransaction(t1);
        this.product.addTransaction(t2);
        assertEquals(2, product.getTransactionsList().size());
    }

    @Test
    void removeTransaction() {
        this.product.addTransaction(t1);
        this.product.addTransaction(t2);
        this.product.removeTransaction(t1);
        assertEquals(1, product.getTransactionsList().size());
    }
}