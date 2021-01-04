package nl.tudelft.sem.transactions.handlers;

import nl.tudelft.sem.transactions.MicroserviceCommunicator;
import nl.tudelft.sem.transactions.entities.Product;
import nl.tudelft.sem.transactions.entities.Transactions;
import nl.tudelft.sem.transactions.entities.TransactionsSplitCredits;
import nl.tudelft.sem.transactions.repositories.ProductRepository;
import nl.tudelft.sem.transactions.repositories.TransactionsRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;


public class TokensValidator extends BaseValidator {
    @Override
    public ResponseEntity<String> handle(Transactions transaction, ProductRepository productRepository, TransactionsRepository transactionsRepository) {

        Product product = productRepository.findByProductId(
                transaction.getProductId()).get(); // we are sure the product is present, otherwise the product validator would have failed
        int portionsLeft = product.getPortionsLeft()
                - transaction.getPortionsConsumed();

            float credits = product.getPrice()
                    / product.getTotalPortions();

            credits = credits * transaction.getPortionsConsumed();
            if (transaction instanceof TransactionsSplitCredits) {
                credits = credits / ((TransactionsSplitCredits) transaction).getUsernames().size();
            }
            credits = Math.round(credits * 100) / 100.f;

            try {
                if (transaction instanceof TransactionsSplitCredits) {
                    System.out.println(((TransactionsSplitCredits) transaction).getUsernames());
                    MicroserviceCommunicator.sendRequestForSplittingCredits(((TransactionsSplitCredits) transaction).getUsernames(), credits);
                } else {
                    MicroserviceCommunicator.sendRequestForChangingCredits(transaction.getUsername(), credits, false);
                }
    
                Transactions trans = new Transactions();
                trans.setProductFk(transaction.getProductFk());
                trans.setPortionsConsumed(transaction.getPortionsConsumed());
                trans.setUsername(transaction.getUsername());
                trans.setTransactionId(transaction.getProductId());
                
                transactionsRepository.save(trans); // TODO what if TransactionsSplitCredits instead of Transactions
                productRepository.updateExistingProduct(product.getProductName(),
                        product.getUsername(), product.getPrice(), product.getTotalPortions(),
                        portionsLeft, 0, product.getProductId());
                
                return super.checkNext(transaction, productRepository, transactionsRepository);
            } catch (DataIntegrityViolationException e) {
                return ResponseEntity.badRequest().body("Adding the transaction failed");
            }

    }
}
