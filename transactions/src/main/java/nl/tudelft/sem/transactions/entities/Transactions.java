package nl.tudelft.sem.transactions.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name = "transaction")
@SuppressWarnings("PMD")
public class Transactions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private int transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = "product_id")
    @JsonManagedReference
    private Product productFk;

    @Column(name = "username")
    private String username;

    @Column(name = "portions_consumed")
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