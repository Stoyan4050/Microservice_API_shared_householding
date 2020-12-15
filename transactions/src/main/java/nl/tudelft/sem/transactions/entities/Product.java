package nl.tudelft.sem.transactions.entities;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


@Entity
@Table(name = "product")
public class Product {

    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "username")
    private String username;

    @Column(name = "price")
    private float price;

    @Column(name = "total_portions")
    private int totalPortions;

    @Column(name = "portions_left")
    private int portionsLeft;

    @Column(name = "expired")
    private int expired;

    @OneToMany(mappedBy = "productFk", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Transactions> transactionsList = new ArrayList<Transactions>();

    /**
     * Constructor for the Product.
     *
     * @param productName   - the productName
     * @param price         - the price
     * @param totalPortions - the number of total portions a product has
     * @param username      - the username of the person who bought the product
     */
    public Product(String productName, float price, int totalPortions, String username) {
        this.productName = productName;
        this.price = price;
        this.totalPortions = totalPortions;
        this.portionsLeft = totalPortions;
        this.expired = 0;
        this.username = username;
    }

    public Product() {
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getTotalPortions() {
        return totalPortions;
    }

    public void setTotalPortions(int totalPortions) {
        this.totalPortions = totalPortions;
    }

    public int getPortionsLeft() {
        return portionsLeft;
    }

    public void setPortionsLeft(int portionsLeft) {
        this.portionsLeft = portionsLeft;
    }

    public int getExpired() {
        return expired;
    }

    public void setExpired(int expired) {
        this.expired = expired;
    }

    public List<Transactions> getTransactionsList() {
        return transactionsList;
    }

    public void setTransactionsList(List<Transactions> transactionsList) {
        this.transactionsList = transactionsList;
    }

    public void removeTransaction(Transactions transaction) {
        this.transactionsList.remove(transaction);
    }

    public void addTransaction(Transactions transaction) {
        this.transactionsList.add(transaction);
    }
}
