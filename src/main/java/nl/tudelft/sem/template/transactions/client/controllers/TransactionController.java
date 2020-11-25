package nl.tudelft.sem.template.transactions.client.controllers;

import nl.tudelft.sem.template.transactions.client.communication.ServerTransactionsCommunication;
import nl.tudelft.sem.template.transactions.server.entities.Transactions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TransactionController {
    ServerTransactionsCommunication con = new ServerTransactionsCommunication();

    public void addNewTransaction(String username, int product_id, int portions){
        if(username.isBlank()){
            return;
        }

        if(portions < 0){
            return;
        }

        con.addTransaction(product_id, username, portions);
    }

    public List<Transactions> getTransactionsByProductId(int product_id) throws IOException {
        List<Transactions> transactionsList = con.getTransactions();
        List<Transactions> transactionsById = new ArrayList<>();

        for(Transactions t : transactionsList){
            if(t.getProduct_id() == product_id){
                transactionsById.add(t);
            }
        }
        return transactionsById;
    }

    public List<Transactions> getTransactionsByUsername(String username) throws IOException {
        List<Transactions> transactionsList = con.getTransactions();
        List<Transactions> transactionsByUsername = new ArrayList<>();

        for(Transactions t : transactionsList){
            if(t.getUsername().equals(username)){
                transactionsByUsername.add(t);
            }
        }
        return transactionsByUsername;
    }

}
