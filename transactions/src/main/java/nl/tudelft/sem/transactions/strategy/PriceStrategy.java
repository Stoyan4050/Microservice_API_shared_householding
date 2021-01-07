package nl.tudelft.sem.transactions.strategy;

import java.util.Comparator;
import java.util.List;
import nl.tudelft.sem.transactions.entities.Product;

/**
 * Provides functionality for sorting products on their price.
 * Implements SortProductsStrategy to adhere to "strategy" design pattern.
 */
public class PriceStrategy implements SortProductsStrategy {

    /**
     * Sorts products in ascending order based on their price.
     *
     * @param products products to be sorted
     */
    @Override
    public void sortProducts(List<Product> products) {
        products.sort(Comparator.comparingDouble(Product::getPrice));
    }
}
