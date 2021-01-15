package nl.tudelft.sem.transactions.handlers;

import java.util.Optional;
import nl.tudelft.sem.transactions.entities.Product;
import nl.tudelft.sem.transactions.entities.Transactions;
import nl.tudelft.sem.transactions.entities.TransactionsSplitCredits;
import nl.tudelft.sem.transactions.repositories.ProductRepository;
import nl.tudelft.sem.transactions.repositories.TransactionsRepository;

public class ValidatorHelper {
    private transient Transactions transaction;
    private transient ProductRepository productRepository;
    private transient TransactionsRepository transactionsRepository;

    /**Constructor for the ValidatorHelper.
     *
     * @param transaction - the transaction of the user
     * @param productRepository - the repository from where we will change the product
     * @param transactionsRepository - the repository where we will add the transaction
     */
    public ValidatorHelper(Transactions transaction,
                           ProductRepository productRepository,
                           TransactionsRepository transactionsRepository) {

        this.transaction = transaction;
        this.productRepository = productRepository;
        this.transactionsRepository = transactionsRepository;
    }

    public Transactions getTransaction() {
        return this.transaction;
    }

    public TransactionsRepository getTransactionsRepository() {
        return this.transactionsRepository;
    }

    public ProductRepository getProductRepository() {
        return productRepository;
    }

    /**
     * Calculate the credits for the product.
     *
     * @param product - the product for which we will calculate credits
     * @return the credits
     */
    public float calculateCredits(Product product) {
        float credits = product.getPrice()
                / product.getTotalPortions();

        credits = credits * transaction.getPortionsConsumed();
        if (transaction instanceof TransactionsSplitCredits) {
            credits = credits / ((TransactionsSplitCredits) transaction).getUsernames().size();
        }
        credits = Math.round(credits * 100) / 100.f;

        return credits;
    }

    public int calculatePortionsLeft() {
        return getProduct().getPortionsLeft()
                - this.transaction.getPortionsConsumed();
    }

    /** Get the product from the repository.
     *
     * @return the product from the helper repository
     */
    public Product getProduct() {
        Optional<Product> transactionProduct = this.productRepository
                .findById(this
                        .getTransaction()
                        .getProductId());

        return transactionProduct.orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        return ((ValidatorHelper) o).getTransaction()
                .getTransactionId() == getTransaction().getTransactionId();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
