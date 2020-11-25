package nl.tudelft.sem.template.transactions.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import nl.tudelft.sem.template.transactions.server.repositories.TransactionsRepository;
import nl.tudelft.sem.template.transactions.server.entities.Transactions;

import java.util.List;

@Controller
public class TransactionController {
    @Autowired
    private TransactionsRepository transactionsRepository;

    @GetMapping("/allTransactions")
    public @ResponseBody
    List<Transactions> getAllTransactions() {
        // This returns a JSON or XML with the reservations
        return transactionsRepository.findAll();
    }

    @PostMapping("/addNewTransaction") // Map ONLY POST Requests
    public @ResponseBody
    boolean addNewTransaction(@RequestBody Transactions transaction) {

        try {
            transactionsRepository.save(transaction);
            return true;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }
}
