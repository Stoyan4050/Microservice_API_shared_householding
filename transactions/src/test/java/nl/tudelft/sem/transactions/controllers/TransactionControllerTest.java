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
        transaction.setPortionsConsumed(portionsConsumed);
        transaction.setProductFk(product);
        transaction.setTransactionId(1);
        transaction.setUsername(username);
    }

    @Test
    public void constructorTest() {
        assertNotNull(transaction);
    }

    @Test
    public void getPortions_consumed() {

        assertEquals(1, transaction.getPortionsConsumed());
    }

    @Test
    public void getProductId() {

        assertEquals(product.getProductId(), transaction.getProductId());
    }

    @Test
    public void getUsername() {

        assertEquals("Stoyan", transaction.getUsername());
    }

    @Test
    public void getProduct() {

        assertEquals(product.getExpired(), transaction.getProductFk().getExpired());
        assertEquals(product.getPortionsLeft(), transaction.getProductFk().getPortionsLeft());
        assertEquals(product.getProductName(), transaction.getProductFk().getProductName());
        Assert.assertEquals(product.getPrice(), transaction.getProductFk().getPrice(), 0.005f);
        assertEquals(product.getTotalPortions(), transaction.getProductFk().getTotalPortions());
        assertEquals(product.getUsername(), transaction.getProductFk().getUsername());
    }

}
