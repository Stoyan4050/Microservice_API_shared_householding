package nl.tudelft.sem.transactions.client.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.transactions.client.communication.ServerTransactionsCommunication;
import nl.tudelft.sem.transactions.entities.Transactions;

@SuppressWarnings("PMD")
public class TransactionController {
    ServerTransactionsCommunication con = new ServerTransactionsCommunication();

    /**
     * New transaction.
     *
     * @param username username of user
     *
     * @param productId Id of product
     *
     * @param portions number of portions
     *
     */
    public void addNewTransaction(String username, int productId, int portions) {

        if (username.isBlank()) {
            return;
        }

        if (portions < 0) {
            return;
        }

        con.addTransaction(productId, username, portions);

    }

    /**
     * Get transactions by ID.
     *
     * @param productId Id of a product
     *
     * @return transactions for this iD
     *
     * @throws IOException when there is an error in Server Communication
     */
    public List<Transactions> getTransactionsByProductId(int productId) throws IOException {
        List<Transactions> transactionsList = con.getTransactions();
        List<Transactions> transactionsById = new ArrayList<>();

        for (Transactions t : transactionsList) {
            if (t.getProductId() == productId) {
                transactionsById.add(t);
            }
        }

        return transactionsById;
    }

    /**
     *Gives all transactions for particular user.
     *
     * @param username username of user
     *
     * @return all transaction for that user
     *
     * @throws IOException when there is an error in Server Communication
     */
    public List<Transactions> getTransactionsByUsername(String username) throws IOException {
        List<Transactions> transactionsList = con.getTransactions();
        List<Transactions> transactionsByUsername = new ArrayList<>();

        for (Transactions t : transactionsList) {
            if (t.getUsername().equals(username)) {
                transactionsByUsername.add(t);
            }
        }
        return transactionsByUsername;
    }

}
