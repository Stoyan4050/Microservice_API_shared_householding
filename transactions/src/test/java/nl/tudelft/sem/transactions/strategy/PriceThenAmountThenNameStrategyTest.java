package nl.tudelft.sem.transactions.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nl.tudelft.sem.transactions.entities.Product;
import org.junit.jupiter.api.Test;

class PriceThenAmountThenNameStrategyTest {

    @Test
    void sortProducts() {
        Product product1 = new Product("Milk", 12.0f, 18, "Bob");
        Product product2 = new Product("Cheese", 14.0f, 7, "Alice");
        Product product3 = new Product("Bread", 7.0f, 3, "Chris");
        Product product4 = new Product("Ham", 1.0f, 9, "Dan");
        Product product5 = new Product("Chocolate", 7.0f, 3, "Dan");
        Product product6 = new Product("Shrimps", 1.0f, 3, "Dan");

        List<Product>
            products = new ArrayList<>(
            Arrays.asList(product1, product2, product3, product4, product5, product6));

        PriceThenAmountThenNameStrategy strategy = new PriceThenAmountThenNameStrategy();
        List<Product> expected = new ArrayList<>(
            Arrays.asList(product6, product4, product3, product5, product1, product2));

        strategy.sortProducts(products);

        assertEquals(expected, products);
    }
}