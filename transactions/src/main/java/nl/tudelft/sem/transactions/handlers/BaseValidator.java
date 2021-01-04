package nl.tudelft.sem.transactions.handlers;

import nl.tudelft.sem.transactions.entities.Product;
import nl.tudelft.sem.transactions.entities.Transactions;
import nl.tudelft.sem.transactions.entities.TransactionsSplitCredits;
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

    protected float calculateCredits(Transactions transaction, Product product) {
        float credits = product.getPrice()
                / product.getTotalPortions();

        credits = credits * transaction.getPortionsConsumed();
        if (transaction instanceof TransactionsSplitCredits) {
            credits = credits / ((TransactionsSplitCredits) transaction).getUsernames().size();
        }
        credits = Math.round(credits * 100) / 100.f;

        return credits;
    }

    protected int calculatePortionsLeft(Transactions transaction, Product product) {
        return product.getPortionsLeft()
                - transaction.getPortionsConsumed();
    }
}
