package nl.tudelft.sem.transactions.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TransactionsSplitCreditsTest {

    private transient TransactionsSplitCredits t1;
    private transient Transactions transactionsSplit;

    @BeforeEach
    void setUp() {
        transactionsSplit = new Transactions();
        List<String> usernames = new ArrayList<>();
        usernames.add("Kendra");
        usernames.add("Oskar");
        usernames.add("Fabian");
        t1 = new TransactionsSplitCredits(usernames);
    }

    @Test
    void emptyConstructor() {
        TransactionsSplitCredits t2 = new TransactionsSplitCredits();
        assertNotNull(t2);
    }

    @Test
    void getUsernames() {
        List<String> expected = Arrays.asList("Kendra", "Oskar", "Fabian");
        assertEquals(expected, t1.getUsernames());
    }

    @Test
    void setUsernames() {
        List<String> usernames2 = new ArrayList<>();
        usernames2.add("Kendra2");
        usernames2.add("Oskar2");
        usernames2.add("Fabian2");
        t1.setUsernames(usernames2);
        assertEquals(usernames2, t1.getUsernames());
    }

    @Test
    void asTransaction() {
        Transactions transaction = t1.asTransaction();
        assertEquals(transaction.getClass(), Transactions.class);
    }

    @Test
    void asTransactionSetProductFk() {
        Product product = new Product("cookies", 2.8f, 3, "stoyan");
        t1.setProduct(product);

        Transactions transaction = t1.asTransaction();

        assertEquals(product, transaction.getProductFk());
    }

    @Test
    void asTransactionGetPortionsConsumed() {
        t1.setPortionsConsumed(3);

        Transactions transaction = t1.asTransaction();

        assertEquals(3, transaction.getPortionsConsumed());
    }

    @Test
    void asTransactionGetTransactionId() {
        t1.setTransactionId(1L);

        Transactions transaction = t1.asTransaction();

        assertEquals(1L, transaction.getTransactionId());
    }

    @Test
    void asTransactionGetTransactionUsername() {
        t1.setUsername("stoyan");

        Transactions transaction = t1.asTransaction();

        assertEquals("stoyan", transaction.getUsername());
    }
}
