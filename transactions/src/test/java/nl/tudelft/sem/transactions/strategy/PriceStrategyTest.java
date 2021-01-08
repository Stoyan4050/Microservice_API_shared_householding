package nl.tudelft.sem.transactions.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nl.tudelft.sem.transactions.entities.Product;
import org.junit.jupiter.api.Test;

class PriceStrategyTest {

    @Test
    void sortProducts() {
        Product product1 = new Product("Milk", 12.0f, 18, "Bob");
        Product product2 = new Product("Cheese", 14.0f, 7, "Alice");
        Product product3 = new Product("Bread", 7.0f, 3, "Chris");
        Product product4 = new Product("Ham", 1.0f, 9, "Dan");

        List<Product> products =
            new ArrayList<>(Arrays.asList(product1, product2, product3, product4));

        PriceStrategy priceStrategy = new PriceStrategy();
        List<Product> expected =
            new ArrayList<>(Arrays.asList(product4, product3, product1, product2));

        priceStrategy.sortProducts(products);

        assertEquals(expected, products);
    }
}