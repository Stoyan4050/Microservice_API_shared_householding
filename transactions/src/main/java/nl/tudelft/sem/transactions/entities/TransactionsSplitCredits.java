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

    /**
     * Create a new Transaction from this TransactionSplitCredits object.
     * Useful when saving TransactionSplitCredits as a Transaction in the database.
     *
     * @return A newly created Transaction from this object.
     */
    public Transactions asTransaction() {
        Transactions transaction = new Transactions();
        transaction.setProductFk(this.getProductFk());
        transaction.setPortionsConsumed(this.getPortionsConsumed());
        transaction.setUsername(this.getUsername());
        transaction.setTransactionId(this.getTransactionId());

        return transaction;
    }
}
