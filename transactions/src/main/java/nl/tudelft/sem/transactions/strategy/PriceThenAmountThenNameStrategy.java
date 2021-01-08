package nl.tudelft.sem.transactions.strategy;

import java.util.List;
import nl.tudelft.sem.transactions.entities.Product;

/**
 * Provides functionality for sorting products primarily on their price,
 * secondarily on number of portions left, ternary on their name.
 * Implements SortProductsStrategy to adhere to "strategy" design pattern.
 */
public class PriceThenAmountThenNameStrategy implements SortProductsStrategy {
    /**
     * Sorts products primarily on their price,
     * secondarily on number of portions left, ternary on their name,
     * all in ascending order.
     *
     * @param products products to be sorted
     */
    @Override
    public void sortProducts(List<Product> products) {
        products.sort((o1, o2) -> {
            if (o1.getPrice() == o2.getPrice()) {
                if (o1.getPortionsLeft() == o2.getPortionsLeft()) {
                    return o1.getProductName().compareTo(o2.getProductName());
                }
                return Integer.compare(o1.getPortionsLeft(), o2.getPortionsLeft());
            }
            return Double.compare(o1.getPrice(), o2.getPrice());
        });
    }
}
