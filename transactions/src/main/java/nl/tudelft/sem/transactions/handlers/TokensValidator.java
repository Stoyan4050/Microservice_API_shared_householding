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
    @Override
    public ResponseEntity<String> handle(Transactions transaction,
                                         ProductRepository productRepository,
                                         TransactionsRepository transactionsRepository) {

        // we are sure the product is present, otherwise the product validator would have failed
        Product product = productRepository.findByProductId(
                transaction.getProductId()).get();

        float credits = calculateCredits(transaction, product);

        // if the credits did not change, return a 204 NO CONTENT http response
        if (credits == 0) {
            return ResponseEntity.noContent().build();
        }

        return super.checkNext(transaction, productRepository, transactionsRepository);
    }
}
