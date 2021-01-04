package nl.tudelft.sem.transactions.handlers;

import nl.tudelft.sem.transactions.entities.Transactions;
import nl.tudelft.sem.transactions.repositories.ProductRepository;
import nl.tudelft.sem.transactions.repositories.TransactionsRepository;
import org.springframework.http.ResponseEntity;

public interface Validator {
    void setNext(Validator handler);
    ResponseEntity<String> handle(Transactions transactions, ProductRepository productRepository, TransactionsRepository transactionsRepository);
}
