package nl.tudelft.sem.transactions.handlers;

import nl.tudelft.sem.transactions.MicroserviceCommunicator;
import nl.tudelft.sem.transactions.entities.Product;
import nl.tudelft.sem.transactions.entities.TransactionsSplitCredits;

public class TransactionCommunicator {
    private final transient ValidatorHelper helper;

    public TransactionCommunicator(ValidatorHelper helper) {

        this.helper = helper;
    }


    /**
     * Saves the transaction in the provided repository.
     */
    public void saveTransaction() {
        if (helper.getTransaction() instanceof TransactionsSplitCredits) {
            helper.getTransactionsRepository()
                    .save(((TransactionsSplitCredits) helper.getTransaction()).asTransaction());
        } else {
            helper.getTransactionsRepository().save(helper.getTransaction());
        }
    }

    /**
     * Updates the product in the proved repository.
     *
     * @param product - the product we will edit
     * @param portionsLeft - the number of portions we will subtract
     */
    public void updateProduct(Product product, int portionsLeft) {
        helper.getProductRepository().updateExistingProduct(product.getProductName(),
                product.getUsername(), product.getPrice(), product.getTotalPortions(),
                portionsLeft, 0, product.getProductId());
    }

    /**
     * Sends request to change the credits of the user/users.
     *
     * @param credits - the credits to be subtracted
     */
    public void requestForTransaction(float credits) {
        if (helper.getTransaction() instanceof TransactionsSplitCredits) {
            MicroserviceCommunicator.sendRequestForSplittingCredits(
                    ((TransactionsSplitCredits) helper.getTransaction()).getUsernames(),
                    credits
            );
        } else {
            MicroserviceCommunicator.sendRequestForChangingCredits(
                    helper.getTransaction().getUsername(),
                    credits, false);
        }
    }

}
