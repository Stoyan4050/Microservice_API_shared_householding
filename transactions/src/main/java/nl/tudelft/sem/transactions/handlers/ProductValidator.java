package nl.tudelft.sem.transactions.handlers;

import nl.tudelft.sem.transactions.MicroserviceCommunicator;
import nl.tudelft.sem.transactions.entities.Product;
import nl.tudelft.sem.transactions.entities.Transactions;
import nl.tudelft.sem.transactions.entities.TransactionsSplitCredits;
import nl.tudelft.sem.transactions.repositories.ProductRepository;
import nl.tudelft.sem.transactions.repositories.TransactionsRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class ProductValidator extends BaseValidator {

    @Override
    public ResponseEntity<String> handle(Transactions transaction, ProductRepository productRepository, TransactionsRepository transactionsRepository) {
        Optional<Product> optionalProduct = productRepository.findByProductId(
                transaction.getProductId());

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            int portionsLeft = product.getPortionsLeft()
                    - transaction.getPortionsConsumed();

            if (transaction.getProductFk().getExpired() == 1 || portionsLeft < 0) {
                return ResponseEntity.badRequest().body(
                        "Product is expired or there is no portions left");
            }

            return super.checkNext(transaction, productRepository, transactionsRepository);

        }
        return ResponseEntity.notFound().build();
    }
}
