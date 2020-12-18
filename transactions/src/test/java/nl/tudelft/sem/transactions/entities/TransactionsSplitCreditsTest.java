package nl.tudelft.sem.transactions.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TransactionsSplitCreditsTest {

    private TransactionsSplitCredits t1;
    private Transactions transactionsSplit;

    @BeforeEach
    void setup() {
        transactionsSplit = new Transactions();
        List<String> usernames = new ArrayList<>();
        usernames.add("Kendra");
        usernames.add("Oskar");
        usernames.add("Fabian");
        t1 = new TransactionsSplitCredits(usernames, transactionsSplit);
    }

    @Test
    void emptyConstructor() {
        TransactionsSplitCredits t2 = new TransactionsSplitCredits();
        assertNotNull(t2);
    }

    @Test
    void getUsernames() {
        List<String> expected = Arrays.asList("Kendra","Oskar","Fabian");
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
    void getTransactionsSplit() {
        assertEquals(transactionsSplit,t1.getTransactionsSplit());
    }

    @Test
    void setTransactionSplit() {
        Transactions ts2 = new Transactions();
        t1.setTransactionsSplit(ts2);
        assertEquals(ts2, t1.getTransactionsSplit());
    }

}