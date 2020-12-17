package nl.tudelft.sem.transactions.entities;

import java.util.List;

public class TransactionsSplitCredits {
    private List<String> usernames;
    private transient Transactions transactionsSplit;

    /**Constructor for transaction for splitting credits.
     *
     * @param usernames List of usernames.
     * @param transactionsSplit transaction, which credits we have to split.
     */
    public TransactionsSplitCredits(List<String> usernames, Transactions transactionsSplit) {
        this.usernames = usernames;
	this.transactionsSplit = transactionsSplit;
	
    }

	public TransactionsSplitCredits() {
    	
	}

public List<String> getUsernames() {
	return usernames;
    }

    public void setUsernames(List<String> usernames) {
	this.usernames = usernames;
    }

    public Transactions getTransactionsSplit() {
	return transactionsSplit;
    }

    public void setTransactions(Transactions transactionsSplit) {
	this.transactionsSplit = transactionsSplit;
    }
}
