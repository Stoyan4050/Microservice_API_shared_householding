package nl.tudelft.sem.transactions.strategy;

import java.util.Comparator;
import java.util.List;
import nl.tudelft.sem.transactions.entities.Product;

/**
 * Provides functionality for sorting products by its name.
 * Implements SortProductsStrategy to adhere to "strategy" design pattern.
 */
public class NameStrategy implements SortProductsStrategy {

    /**
     * Sorts products lexicographically based on their name.
     *
     * @param products products to be sorted
     */
    @Override
    public void sortProducts(List<Product> products) {
        products.sort(Comparator.comparing(Product::getProductName));
    }
}
