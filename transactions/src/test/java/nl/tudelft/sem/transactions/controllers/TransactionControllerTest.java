package nl.tudelft.sem.transactions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import nl.tudelft.sem.transactions.entities.Product;
import nl.tudelft.sem.transactions.entities.Transactions;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



@SuppressWarnings("PMD")
class TransactionControllerTest {
    private Product product;
    private final Transactions transaction = new Transactions();


    @BeforeEach
    public void setup() {
        product = new Product("bread", (float) 1.50, 10, "Kristen");
        int portionsConsumed = 1;
        String username = "Stoyan";
        transaction.setPortions_consumed(portionsConsumed);
        transaction.setProduct(product);
        transaction.setTransaction_id(1);
        transaction.setUsername(username);
    }

    @Test
    public void constructorTest() {
        assertNotNull(transaction);
    }

    @Test
    public void getPortions_consumed() {

        assertEquals(1, transaction.getPortions_consumed());
    }

    @Test
    public void getProductId() {

        assertEquals(product.getProductId(), transaction.getProduct_id());
    }

    @Test
    public void getUsername() {

        assertEquals("Stoyan", transaction.getUsername());
    }

    @Test
    public void getProduct() {

        assertEquals(product.getExpired(), transaction.getProduct().getExpired());
        assertEquals(product.getPortionsLeft(), transaction.getProduct().getPortionsLeft());
        assertEquals(product.getProductName(), transaction.getProduct().getProductName());
        Assert.assertEquals(product.getPrice(), transaction.getProduct().getPrice(), 0.005f);
        assertEquals(product.getTotalPortions(), transaction.getProduct().getTotalPortions());
        assertEquals(product.getUsername(), transaction.getProduct().getUsername());
    }

}
