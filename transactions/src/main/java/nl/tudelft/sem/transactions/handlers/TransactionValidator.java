package nl.tudelft.sem.transactions.handlers;

import com.netflix.discovery.EurekaClient;
import nl.tudelft.sem.transactions.MicroserviceCommunicator;
import nl.tudelft.sem.transactions.entities.Product;
import nl.tudelft.sem.transactions.entities.Transactions;
import nl.tudelft.sem.transactions.entities.TransactionsSplitCredits;
import nl.tudelft.sem.transactions.repositories.ProductRepository;
import nl.tudelft.sem.transactions.repositories.TransactionsRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;

public class TransactionValidator extends BaseValidator {
    private transient EurekaClient discoveryClient;
    private transient String username;

    public TransactionValidator(EurekaClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }


    @Override
    public ResponseEntity<String> handle(Transactions transaction,
                                         ProductRepository productRepository,
                                         TransactionsRepository transactionsRepository) {

        // we are sure the product is present, otherwise the product validator would have failed
        Product product = productRepository.findByProductId(
                transaction.getProductId()).get();

        float credits = calculateCredits(transaction, product);
        int portionsLeft = calculatePortionsLeft(transaction, product);

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

            username = transaction.getUsername();

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
            // if the discovery client is not autowired properly,
            // don't bother trying to reach the requests microservice
            if (discoveryClient == null) {
                return ResponseEntity.ok().body("Transaction was successfully added");
            }
            return ResponseEntity.ok()
                    .body("Transaction was successfully added. Remaining credits for "
                            + username + ": "
                            + MicroserviceCommunicator.getCredits(username, discoveryClient));
        }
        return next.handle(transactions, productRepository, transactionsRepository);
    }
}
