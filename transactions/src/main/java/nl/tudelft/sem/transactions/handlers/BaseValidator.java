package nl.tudelft.sem.transactions.handlers;

import nl.tudelft.sem.transactions.entities.Transactions;
import nl.tudelft.sem.transactions.repositories.ProductRepository;
import nl.tudelft.sem.transactions.repositories.TransactionsRepository;
import org.springframework.http.ResponseEntity;

public abstract class BaseValidator implements Validator {
    protected transient Validator next;

    public void setNext(Validator v) {
        this.next = v;
    }

    protected ResponseEntity<String> checkNext(Transactions transactions,
                                               ProductRepository productRepository,
                                               TransactionsRepository transactionsRepository) {
        if (next == null) {
            return ResponseEntity.ok().body("Transaction was successfully added");
        }
        return next.handle(transactions, productRepository, transactionsRepository);
    }
}
