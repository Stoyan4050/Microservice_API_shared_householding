package nl.tudelft.sem.template.transactions.server.entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("PMD")
class TransactionsTest {
    private Transactions transactions;

    @BeforeEach
    void beforeEach() {
        this.transactions = new Transactions();
    }

    @Test
    void transactionsConstructor() {
        Assertions.assertNotNull(transactions);
    }

    @Test
    void getTransaction_id() {
        transactions.setTransaction_id(42);
        Assertions.assertEquals(42, transactions.getTransaction_id());
    }

    @Test
    void setTransaction_id() {
        transactions.setTransaction_id(53);
        Assertions.assertEquals(53, transactions.getTransaction_id());
    }

    @Test
    void getProduct() {
        Product product = new Product("Milk", 12.0f, 12);
        transactions.setProduct(product);
        Assertions.assertEquals(product, transactions.getProduct());
    }

    @Test
    void setProduct() {
        Product product = new Product("Bread", 42.0f, 87);
        transactions.setProduct(product);
        Assertions.assertEquals(product, transactions.getProduct());
    }

    @Test
    void getUsername() {
        String bob = "bob";
        transactions.setUsername(bob);
        Assertions.assertEquals(bob, transactions.getUsername());
    }

    @Test
    void setUsername() {
        String alice = "alice";
        transactions.setUsername(alice);
        Assertions.assertEquals(alice, transactions.getUsername());
    }

    @Test
    void getPortions_consumed() {
        transactions.setPortions_consumed(48);
        Assertions.assertEquals(48, transactions.getPortions_consumed());
    }

    @Test
    void setPortions_consumed() {
        transactions.setPortions_consumed(128);
        Assertions.assertEquals(128, transactions.getPortions_consumed());
    }
}