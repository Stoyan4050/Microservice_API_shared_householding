package nl.tudelft.sem.transactions.handlers;

import com.netflix.discovery.EurekaClient;
import nl.tudelft.sem.transactions.MicroserviceCommunicator;
import nl.tudelft.sem.transactions.entities.Product;
import nl.tudelft.sem.transactions.entities.Transactions;
import nl.tudelft.sem.transactions.entities.TransactionsSplitCredits;
import nl.tudelft.sem.transactions.repositories.ProductRepository;
import nl.tudelft.sem.transactions.repositories.TransactionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;


public class TokensValidator extends BaseValidator {
    private transient EurekaClient discoveryClient;
    private transient String username;

    public TokensValidator(EurekaClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @Override
    public ResponseEntity<String> handle(Transactions transaction,
                                         ProductRepository productRepository,
                                         TransactionsRepository transactionsRepository) {

        // we are sure the product is present, otherwise the product validator would have failed
        Product product = productRepository.findByProductId(
                transaction.getProductId()).get();
        int portionsLeft = product.getPortionsLeft()
                - transaction.getPortionsConsumed();

        float credits = product.getPrice()
                / product.getTotalPortions();

        credits = credits * transaction.getPortionsConsumed();
        if (transaction instanceof TransactionsSplitCredits) {
            credits = credits / ((TransactionsSplitCredits) transaction).getUsernames().size();
        }
        credits = Math.round(credits * 100) / 100.f;

        // if the credits did not change, return a 204 NO CONTENT http response
        if (credits == 0) {
            return ResponseEntity.noContent().build();
        }
        username = transaction.getUsername();

        try {
            if (transaction instanceof TransactionsSplitCredits) {
                System.out.println(((TransactionsSplitCredits) transaction).getUsernames());
                MicroserviceCommunicator.sendRequestForSplittingCredits(
                        ((TransactionsSplitCredits) transaction).getUsernames(),
                        credits
                );
            } else {
                MicroserviceCommunicator.sendRequestForChangingCredits(transaction.getUsername(),
                        credits, false);
            }

            if (transaction instanceof TransactionsSplitCredits) {
                transactionsRepository
                        .save(((TransactionsSplitCredits) transaction).asTransaction());
            } else {
                transactionsRepository.save(transaction);
            }
            productRepository.updateExistingProduct(product.getProductName(),
                    product.getUsername(), product.getPrice(), product.getTotalPortions(),
                    portionsLeft, 0, product.getProductId());

            return this.checkNext(transaction, productRepository, transactionsRepository);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body("Adding the transaction failed");
        }

    }

    @Override
    protected ResponseEntity<String> checkNext(Transactions transactions,
                                               ProductRepository productRepository,
                                               TransactionsRepository transactionsRepository) {
        if (next == null) {
            return ResponseEntity.ok()
                    .body("Transaction was successfully added. Remaining credits for "
                            + username + ": "
                            + MicroserviceCommunicator.getCredits(username, discoveryClient));
        }
        return next.handle(transactions, productRepository, transactionsRepository);
    }
}
