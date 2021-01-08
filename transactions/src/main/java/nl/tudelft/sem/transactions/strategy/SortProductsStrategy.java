package nl.tudelft.sem.transactions.strategy;

import java.util.List;
import nl.tudelft.sem.transactions.entities.Product;

/**
 * Interface that provides a blueprint for different sorting strategies of products.
 * Introduced to adhere to "strategy" design pattern.
 */
public interface SortProductsStrategy {
    /**
     * Sorts products.
     *
     * @param products products to be sorted
     */
    void sortProducts(List<Product> products);
}
