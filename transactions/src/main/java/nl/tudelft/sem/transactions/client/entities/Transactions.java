package nl.tudelft.sem.transactions.client.entities;

public class Transactions {
    private int transactionId;

    private int productId;

    private String username;

    private int portionsConsumed;

    public Transactions() {
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getPortionsConsumed() {
        return portionsConsumed;
    }

    public void setPortionsConsumed(int portionsConsumed) {
        this.portionsConsumed = portionsConsumed;
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
