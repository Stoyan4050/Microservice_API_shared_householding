package nl.tudelft.sem.transactions.controllers;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.transactions.MicroserviceCommunicator;
import nl.tudelft.sem.transactions.entities.Product;
import nl.tudelft.sem.transactions.entities.Transactions;
import nl.tudelft.sem.transactions.entities.TransactionsSplitCredits;
import nl.tudelft.sem.transactions.repositories.ProductRepository;
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

    @Autowired
    private transient ProductRepository productRepository;

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
        
        Product product = productRepository.findByProductId(transaction.getProductId());
        if (product == null) {
            return false;
        }
        
        int portionsLeft = product.getPortionsLeft()
                                   - transaction.getPortionsConsumed();
        
        if (transaction.getProductFk().getExpired() == 1 || portionsLeft < 0) {
            return false;
        }
        
        float credits = product.getPrice()
                                / product.getTotalPortions();
        
        credits = credits * transaction.getPortionsConsumed();
        credits = Math.round(credits * 100) / 100;
        
    
        try {
            transactionsRepository.save(transaction);
            productRepository.updateExistingProduct(product.getProductName(),
                    product.getUsername(), product.getPrice(), product.getTotalPortions(),
                    portionsLeft, 0, product.getProductId());
            
            MicroserviceCommunicator.sendRequestForChangingCredits(transaction.getUsername(),
                    credits, false);
            return true;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }

    /**New transaction which credits will be split among the users that are eating together.
     *
     * @param transactionsSplitCredits List of usernames and float with the credits
     *                                that we will have to subtract
     * @return true if the the credits were evenly distributed and subtracted from the users.
     */
    @PostMapping("/transactionSplittingCredits")
    public @ResponseBody
    boolean addNewTransactionSplittingCredits(@RequestBody TransactionsSplitCredits
                                                      transactionsSplitCredits) {
        
        
        Transactions transaction = transactionsSplitCredits.getTransactionsSplit();
        
        Product product = productRepository.findByProductId(transaction.getProductId());
        
        if (product == null) {
            return false;
        }
        
        int portionsLeft = product.getPortionsLeft()
                                   - transaction.getPortionsConsumed();
    
        if (product.getExpired() == 1 || portionsLeft < 0) {
            return false;
        }
    
        List<String> usernames = transactionsSplitCredits.getUsernames();
        float credits = product.getPrice()
                                / product.getTotalPortions();
        
        credits = credits * transaction.getPortionsConsumed();
        float splitCredits = credits / usernames.size();
        
        splitCredits = Math.round(splitCredits * 100) / 100;
        
        try {
            productRepository.updateExistingProduct(product.getProductName(),
                    product.getUsername(), product.getPrice(),
                    product.getTotalPortions(), portionsLeft, 0, product.getProductId());
            
            transactionsRepository.save(transaction);
            MicroserviceCommunicator.sendRequestForSplittingCredits(usernames, splitCredits);
            
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
    boolean editTransactions(@RequestBody Transactions transaction) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request
        //Transactions oldTransaction =
        // transactionsRepository.getOne(transaction.getTransactionId());
        //Product product = oldTransaction.getProductFk();
        //oldTransaction.setPortionsConsumed(
        // product.getPortionsLeft() + oldTransaction.getPortionsConsumed());
        
        //float pricePerPortion = product.getPrice() / product.getTotalPortions();
        
        
        
        
        try {
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
    public void deleteTransaction(@PathVariable(value = "transactionId") long transactionId) {
        try {
            Optional<Transactions> t = transactionsRepository.findById(transactionId);
            Transactions transaction = t.get();
            transaction.getProductFk().removeTransaction(transaction);
            transaction.setProductFk(null);
            transactionsRepository.delete(transaction);
            System.out.println("The transaction was deleted.");
        } catch (Exception e) {
            throw new IllegalArgumentException("the deletion has failed");
        }
    }
}
