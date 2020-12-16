package nl.tudelft.sem.transactions.controllers;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.transactions.entities.Transactions;
import nl.tudelft.sem.transactions.repositories.TransactionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
    boolean addNewTransaction(@RequestBody Transactions transaction) {

        try {
            transactionsRepository.save(transaction);
            return true;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }

    /**
     * Edits a transaction in the database.
     *
     * @param transaction - transaction to be updated
     * @return true if transaction was updated
     */
    @RequestMapping("/editTransaction")
    public @ResponseBody
    boolean editHolidays(@RequestBody Transactions transaction) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request
        try {
            return transactionsRepository.updateExistingTransaction(transaction.getProductId(),
                transaction.getUsername(),
                transaction.getPortionsConsumed(),
                transaction.getTransactionId()) == 1;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Deletes a transaction.
     *
     * @param transactionId - id of the transaction to be deleted.
     * @author - Kendra Sartori
     */
    @DeleteMapping("/deleteTransaction/{transactionId}")
    @ResponseBody
    public void deleteTransaction(@PathVariable(value = "transactionId") long transactionId) {
        try {
            Optional<Transactions> t = transactionsRepository.findById(transactionId);
            Transactions transaction = t.get();
            transaction.getProduct().removeTransaction(transaction);
            transaction.setProduct(null);
            transactionsRepository.delete(transaction);
            System.out.println("The transaction was deleted.");
        } catch (Exception e) {
            throw new IllegalArgumentException("the deletion has failed");
        }
    }
}
