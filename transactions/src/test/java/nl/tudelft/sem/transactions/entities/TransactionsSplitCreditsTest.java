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
    void setup() {
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
}
