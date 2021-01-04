package nl.tudelft.sem.transactions.entities;

import java.util.List;

public class TransactionsSplitCredits extends Transactions {
    private List<String> usernames;

    /**
     * Constructor for transaction for splitting credits.
     *
     * @param usernames         List of usernames.
     */
    public TransactionsSplitCredits(List<String> usernames) {
        super();
        this.usernames = usernames;
    }

    public TransactionsSplitCredits() {
    }

    public List<String> getUsernames() {
        return usernames;
    }

    public void setUsernames(List<String> usernames) {
        this.usernames = usernames;
    }
}
