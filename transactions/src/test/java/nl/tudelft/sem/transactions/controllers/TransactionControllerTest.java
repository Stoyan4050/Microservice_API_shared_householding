package nl.tudelft.sem.transactions.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.transactions.MicroserviceCommunicator;
import nl.tudelft.sem.transactions.entities.Product;
import nl.tudelft.sem.transactions.entities.Transactions;
import nl.tudelft.sem.transactions.repositories.TransactionsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

@SuppressWarnings("PMD")
class TransactionControllerTest {
    @Mock
    TransactionsRepository transactionsRepository;

    @Mock
    MicroserviceCommunicator microserviceCommunicator;

    @InjectMocks
    TransactionController transactionController;

    Transactions transaction;

    @BeforeEach
    void setUp() {
        transactionController = spy(TransactionController.class);
        MockitoAnnotations.initMocks(this);
        transaction = new Transactions();
        transaction.setTransactionId(1L);
        transaction.setPortionsConsumed(2);
        transaction.setUsername("Bob");
        Product product = new Product();
        product.setProductId(4);
        transaction.setProduct(product);
    }

    @Test
    void editTransaction() {
        doReturn(1).when(transactionsRepository)
            .updateExistingTransaction(4, "Bob", 2, 1L);

        boolean result = transactionController.editTransactions(transaction);

        verify(transactionsRepository)
            .updateExistingTransaction(4, "Bob", 2, 1L);
        assertTrue(result);
    }

    @Test
    void deleteTransaction() {
        doReturn(Optional.of(transaction)).when(transactionsRepository).findById(1L);
        doNothing().when(transactionsRepository).delete(transaction);

        transactionController.deleteTransaction(1L);

        verify(transactionsRepository).findById(1L);
        verify(transactionsRepository).delete(transaction);
    }

    @Test
    void getAllTransactions() {
        List<Transactions> transactions = List.of(transaction);
        doReturn(transactions).when(transactionsRepository).findAll();

        List<Transactions> result = transactionController.getAllTransactions();

        verify(transactionsRepository).findAll();

        assertEquals(transactions, result);
    }

    @Test
    void addNewTransaction() {

        boolean result = transactionController.addNewTransaction(transaction);

        verify(transactionsRepository).save(transaction);

        assertTrue(result);
    }

    @Test
    void addNewTransactionFalse() {
        doThrow(DataIntegrityViolationException.class).when(transactionsRepository)
            .save(transaction);

        assertFalse(transactionController.addNewTransaction(transaction));
    }

}
