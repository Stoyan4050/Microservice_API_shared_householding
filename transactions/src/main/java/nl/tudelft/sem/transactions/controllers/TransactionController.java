package nl.tudelft.sem.transactions.controllers;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.transactions.MicroserviceCommunicator;
import nl.tudelft.sem.transactions.entities.Product;
import nl.tudelft.sem.transactions.entities.Transactions;
import nl.tudelft.sem.transactions.entities.TransactionsSplitCredits;
import nl.tudelft.sem.transactions.handlers.Validator;
import nl.tudelft.sem.transactions.repositories.ProductRepository;
import nl.tudelft.sem.transactions.repositories.TransactionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TransactionController {
    @Autowired
    private TransactionsRepository transactionsRepository;

    @Autowired
    private transient ProductRepository productRepository;

    @Autowired
    private transient Validator handler;

    public TransactionsRepository getTransactionsRepository() {
        return transactionsRepository;
    }

    public void setTransactionsRepository(
        TransactionsRepository transactionsRepository) {
        this.transactionsRepository = transactionsRepository;
    }

    @GetMapping("/allTransactions")
    public @ResponseBody
    List<Transactions> getAllTransactions() {
        // This returns a JSON or XML with the reservations
        return transactionsRepository.findAll();
    }

    /**
     * Add new transaction.
     *
     * @param transaction the transaction to be added in the repository
     * @return true if the transaction was added to the repository
     */
    @PostMapping("/addNewTransaction")
    public @ResponseBody
    ResponseEntity<String> addNewTransaction(@RequestBody Transactions transaction) {

        return handler.handle(transaction, productRepository, transactionsRepository);
    }

    /**
     * New transaction which credits will be split among the users that are eating together.
     *
     * @param transactionsSplitCredits List of usernames and float with the credits
     *                                 that we will have to subtract
     * @return true if the the credits were evenly distributed and subtracted from the users.
     */
    @PostMapping("/transactionSplittingCredits")
    public @ResponseBody
    ResponseEntity<String> addNewTransactionSplittingCredits(@RequestBody TransactionsSplitCredits
                                                                 transactionsSplitCredits) {

        return handler.handle(transactionsSplitCredits, productRepository, transactionsRepository);
    }

    /**
     * Edits a transaction in the database. Can not change the username for the transaction!
     *
     * @param transaction - transaction to be updated
     * @return true if transaction was updated
     */
    // TODO validators
    @RequestMapping("/editTransaction")
    public @ResponseBody
    boolean editTransactions(@RequestBody Transactions transaction) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request

        Transactions oldTransaction = transactionsRepository.getOne(transaction.getTransactionId());
        Optional<Product> products = productRepository
                .findByProductId(transaction.getProductFk().getProductId());
        Product product;
        if (!products.isPresent()) {
            return false;
        } else {
            product = products.get();
        }

        float pricePerPortion = product.getPrice() / product.getTotalPortions(); //NOPMD


        product.setPortionsLeft(product.getPortionsLeft() + oldTransaction.getPortionsConsumed());

        if (product.getExpired() == 1
            || (product.getPortionsLeft() - transaction.getPortionsConsumed() < 0)) {
            return false;
        }

        float creditsForOldTransaction = pricePerPortion * oldTransaction.getPortionsConsumed();

        try {
            MicroserviceCommunicator.sendRequestForChangingCredits(oldTransaction.getUsername(),
                creditsForOldTransaction, true);
        } catch (Exception e) {
            return false;
        }

        try {
            MicroserviceCommunicator.sendRequestForChangingCredits(transaction.getUsername(),
                pricePerPortion * transaction.getPortionsConsumed(), false);
        } catch (Exception e) {
            return false;
        }

        try {
            productRepository.updateExistingProduct(product.getProductName(), product.getUsername(),
                product.getPrice(),
                product.getTotalPortions(),
                product.getPortionsLeft() - transaction.getPortionsConsumed(),
                product.getExpired(), product.getProductId());

            if (transactionsRepository.updateExistingTransaction(transaction.getProductId(),
                transaction.getUsername(),
                transaction.getPortionsConsumed(),
                transaction.getTransactionId()) == 0) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Deletes a transaction.
     *
     * @param transactionId - id of the transaction to be deleted.
     * @author - Kendra Sartori
     */
    @DeleteMapping("/deleteTransaction/{transactionId}")
    @ResponseBody
    public ResponseEntity<String> deleteTransaction(@PathVariable long transactionId) {
        Optional<Transactions> optTransactions = transactionsRepository.findById(transactionId);
        if (optTransactions.isPresent()) {
            try {
                Transactions transaction = optTransactions.get();
                transaction.getProductFk().removeTransaction(transaction);
                transaction.setProductFk(null);
                transactionsRepository.delete(transaction);
                return ResponseEntity.ok().body("Transaction successfully deleted.");
            } catch (Exception e) {
                throw new IllegalArgumentException("the deletion has failed");
            }
        }
        return ResponseEntity.notFound().build();
    }
}
