package nl.tudelft.sem.template.transactions.server.entities;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
public class Transactions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int transactionId;


    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = "product_id")
    private Product productFk;

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

    public Product getProduct() {
        return this.productFk;
    }

    public void setProduct(Product product) {
        this.productFk = product;
    }

    public int getProduct_id() {
        return productFk.getProductId();
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