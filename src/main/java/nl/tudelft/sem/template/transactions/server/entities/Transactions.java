package nl.tudelft.sem.template.transactions.server.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Transactions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int transactionId;

    private int productId;

    private String username;

    private int portionsConsumed;

    public Transactions() {
    }

    public int getTransaction_id() {
        return transactionId;
    }

    public void setTransaction_id(int transactionId) {
        this.transactionId = transactionId;
    }


    public int getProduct_id() {
        return productId;
    }

    public void setProduct_id(int productId) {
        this.productId = productId;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public int getPortions_consumed() {
        return portionsConsumed;
    }

    public void setPortions_consumed(int portionsConsumed) {
        this.portionsConsumed = portionsConsumed;
    }
}