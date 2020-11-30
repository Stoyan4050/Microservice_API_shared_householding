package nl.tudelft.sem.template.transactions.server.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


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

    /**
     * Constructor for the Product.
     *
     * @param productName   - the productName
     * @param price         - the price
     * @param totalPortions - the number of total portions a product has
     */
    public Product(String productName, float price, int totalPortions) {
        this.productName = productName;
        this.price = price;
        this.totalPortions = totalPortions;
        this.portionsLeft = totalPortions;
        this.expired = 0;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getUsername() {
        return username;
    }

    public float getPrice() {
        return price;
    }

    public int getTotalPortions() {
        return totalPortions;
    }

    public int getPortionsLeft() {
        return portionsLeft;
    }

    public int getExpired() {
        return expired;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setTotalPortions(int totalPortions) {
        this.totalPortions = totalPortions;
    }

    public void setPortionsLeft(int portionsLeft) {
        this.portionsLeft = portionsLeft;
    }
}
