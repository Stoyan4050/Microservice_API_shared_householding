package nl.tudelft.sem.transactions.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "transaction")
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

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public Product getProduct() {
        return this.productFk;
    }

    public void setProduct(Product product) {
        this.productFk = product;
    }

    public int getProductId() {
        return productFk.getProductId();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Product getProductFk() {
        return productFk;
    }

    public void setProductFk(Product productFk) {
        this.productFk = productFk;
    }

    public int getPortionsConsumed() {
        return portionsConsumed;
    }

    public void setPortionsConsumed(int portionsConsumed) {
        this.portionsConsumed = portionsConsumed;
    }
}