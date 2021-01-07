package nl.tudelft.sem.transactions.strategy;

import java.util.Comparator;
import java.util.List;
import nl.tudelft.sem.transactions.entities.Product;

/**
 * Provides functionality for sorting products by amount left.
 * Implements SortProductsStrategy to adhere to "strategy" design pattern.
 */
public class AmountStrategy implements SortProductsStrategy {

    /**
     * Sorts products in ascending order based on the number of portions left.
     *
     * @param products products to be sorted
     */
    @Override
    public void sortProducts(List<Product> products) {
        products.sort(Comparator.comparingInt(Product::getPortionsLeft));
    }
}
