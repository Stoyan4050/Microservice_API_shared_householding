package nl.tudelft.sem.transactions.strategy;

import java.util.Collections;
import java.util.List;
import nl.tudelft.sem.transactions.entities.Product;

/**
 * Provides functionality for shuffling products.
 * Implements SortProductsStrategy to adhere to "strategy" design pattern.
 */
public class RandomStrategy implements SortProductsStrategy {
    /**
     * Sorts products in random order.
     *
     * @param products products to be sorted
     */
    @Override
    public void sortProducts(List<Product> products) {
        Collections.shuffle(products);
    }
}
