package nl.tudelft.sem.transactions.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.transactions.entities.Product;
import nl.tudelft.sem.transactions.entities.Transactions;
import nl.tudelft.sem.transactions.entities.TransactionsSplitCredits;
import nl.tudelft.sem.transactions.handlers.ProductValidator;
import nl.tudelft.sem.transactions.handlers.TokensValidator;
import nl.tudelft.sem.transactions.handlers.Validator;
import nl.tudelft.sem.transactions.repositories.ProductRepository;
import nl.tudelft.sem.transactions.repositories.TransactionsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;

class TransactionControllerTest {
    private static final String BOB = "bob";
    @Mock
    private transient TransactionsRepository transactionsRepository;
    @Mock
    private transient ProductRepository productRepository;
    @Mock
    private transient TransactionsSplitCredits transactionsSplitCredits;

    @Mock
    private transient Validator handler;
    @InjectMocks
    private transient TransactionController transactionController;
    private transient Transactions transaction;
    private transient Product product;
    private transient Validator validator;

    @BeforeEach
    void setUp() {
        transactionController = spy(TransactionController.class);
        MockitoAnnotations.initMocks(this);
        transaction = new Transactions();
        transaction.setTransactionId(1L);
        transaction.setPortionsConsumed(2);
        transaction.setUsername(BOB);
        product = new Product();
        product.setProductId(4);
        transaction.setProductFk(product);
        product.setExpired(0);
        product.setPortionsLeft(5);

        validator = new ProductValidator();
        validator.setNext(new TokensValidator());
    }

    @Test
    void editTransaction() {
        doReturn(transaction).when(transactionsRepository).getOne(1L);
        doReturn(1).when(transactionsRepository)
            .updateExistingTransaction(4, BOB, 2, 1L);

        boolean result = transactionController.editTransactions(transaction);

        verify(transactionsRepository)
            .updateExistingTransaction(4, BOB, 2, 1L);
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
        doReturn(Optional.of(product)).when(productRepository).findByProductId(4L);
        doReturn(validator.handle(transaction, productRepository, transactionsRepository)).when(handler).handle(transaction, productRepository, transactionsRepository);

        ResponseEntity<String> result = transactionController.addNewTransaction(transaction);

        verify(transactionsRepository).save(transaction);

        assertEquals(ResponseEntity.ok().body("Transaction was successfully added"),
            result);
    }

    @Test
    void addNewTransactionProductNotPresent() {
        doReturn(Optional.empty()).when(productRepository).findByProductId(4L);
        doReturn(validator.handle(transaction, productRepository, transactionsRepository)).when(handler).handle(transaction, productRepository, transactionsRepository);

        ResponseEntity<String> result = transactionController.addNewTransaction(transaction);

        assertEquals(ResponseEntity.notFound().build(),
            result);
    }

    @Test
    void addNewTransactionFalse() {
        doReturn(Optional.of(product)).when(productRepository).findByProductId(4L);

        doThrow(DataIntegrityViolationException.class).when(transactionsRepository)
            .save(transaction);
        doReturn(validator.handle(transaction, productRepository, transactionsRepository)).when(handler).handle(transaction, productRepository, transactionsRepository);

        ResponseEntity<String> result = transactionController.addNewTransaction(transaction);

        assertEquals(ResponseEntity.badRequest().body("Adding the transaction failed"), result);
    }

    @Test
    void addNewTransactionSplittingCredits() {
//        doReturn(transaction).when(transactionsSplitCredits).getTransactionsSplit();
//        doReturn(validator).when(handler).handle(transaction, productRepository, transactionsRepository);
        doReturn(Optional.of(product)).when(productRepository).findByProductId(4);

        // mock TransactionsSplitCredits
        doReturn(List.of(BOB)).when(transactionsSplitCredits).getUsernames();
        doReturn(transaction.getTransactionId()).when(transactionsSplitCredits).getTransactionId();
        doReturn(transaction.getProductId()).when(transactionsSplitCredits).getProductId();
        doReturn(transaction.getProductFk()).when(transactionsSplitCredits).getProductFk();
        doReturn(transaction.getUsername()).when(transactionsSplitCredits).getUsername();
        doReturn(transaction.getPortionsConsumed()).when(transactionsSplitCredits).getPortionsConsumed();
        doReturn(transaction).when(transactionsSplitCredits).asTransaction();

        doReturn(validator.handle(transactionsSplitCredits, productRepository, transactionsRepository)).when(handler).handle(transactionsSplitCredits, productRepository, transactionsRepository);

        ResponseEntity<String> result = transactionController
            .addNewTransactionSplittingCredits(transactionsSplitCredits);

        verify(transactionsRepository).save(transaction);

        assertEquals(ResponseEntity.ok().body("Transaction was successfully added"),
            result);
    }

    @Test
    void addNewTransactionSplittingCreditsNullProduct() {
//        doReturn(transaction).when(transactionsSplitCredits).getTransactionsSplit();
        doReturn(Optional.empty()).when(productRepository).findByProductId(4);
        doReturn(validator.handle(transactionsSplitCredits, productRepository, transactionsRepository)).when(handler).handle(transactionsSplitCredits, productRepository, transactionsRepository);

        ResponseEntity<String> result = transactionController
            .addNewTransactionSplittingCredits(transactionsSplitCredits);
        assertEquals(ResponseEntity.notFound().build(), result);
    }

    @Test
    void addNewTransactionSplittingCreditsExpiredProduct() {
        transaction.getProductFk().setExpired(1);
//        doReturn(transaction).when(transactionsSplitCredits).getTransactionsSplit();
        doReturn(Optional.of(product)).when(productRepository).findByProductId(4);

        // mock TransactionsSplitCredits
        doReturn(transaction.getTransactionId()).when(transactionsSplitCredits).getTransactionId();
        doReturn(transaction.getProductId()).when(transactionsSplitCredits).getProductId();
        doReturn(transaction.getProductFk()).when(transactionsSplitCredits).getProductFk();
        doReturn(transaction.getUsername()).when(transactionsSplitCredits).getUsername();
        doReturn(transaction.getPortionsConsumed()).when(transactionsSplitCredits).getPortionsConsumed();
        doReturn(transaction).when(transactionsSplitCredits).asTransaction();
        doReturn(validator.handle(transactionsSplitCredits, productRepository, transactionsRepository)).when(handler).handle(transactionsSplitCredits, productRepository, transactionsRepository);

        ResponseEntity<String> result = transactionController
            .addNewTransactionSplittingCredits(transactionsSplitCredits);
        assertEquals(ResponseEntity.badRequest().body(
            "Product is expired or there is no portions left"), result);
    }

    @Test
    void addNewTransactionSplittingCreditsNoPortionsLeft() {
        transaction.getProductFk().setPortionsLeft(-1);
//        doReturn(transaction).when(transactionsSplitCredits).getTransactionsSplit();
        doReturn(Optional.of(product)).when(productRepository).findByProductId(4);

        // mock TransactionsSplitCredits
        doReturn(transaction.getTransactionId()).when(transactionsSplitCredits).getTransactionId();
        doReturn(transaction.getProductId()).when(transactionsSplitCredits).getProductId();
        doReturn(transaction.getProductFk()).when(transactionsSplitCredits).getProductFk();
        doReturn(transaction.getUsername()).when(transactionsSplitCredits).getUsername();
        doReturn(transaction.getPortionsConsumed()).when(transactionsSplitCredits).getPortionsConsumed();
        doReturn(transaction).when(transactionsSplitCredits).asTransaction();
        doReturn(validator.handle(transactionsSplitCredits, productRepository, transactionsRepository)).when(handler).handle(transactionsSplitCredits, productRepository, transactionsRepository);

        ResponseEntity<String> result = transactionController
            .addNewTransactionSplittingCredits(transactionsSplitCredits);
        assertEquals(ResponseEntity.badRequest().body(
            "Product is expired or there is no portions left"), result);
    }

    @Test
    void addNewTransactionSplittingCreditsDataIntegrityViolation() {
//        doReturn(transaction).when(transactionsSplitCredits).getTransactionsSplit();
        doReturn(Optional.of(product)).when(productRepository).findByProductId(4);
        doReturn(List.of(BOB)).when(transactionsSplitCredits).getUsernames();

        doThrow(DataIntegrityViolationException.class).when(transactionsRepository)
            .save(transaction);

        // mock TransactionsSplitCredits should be last
        doReturn(transaction.getTransactionId()).when(transactionsSplitCredits).getTransactionId();
        doReturn(transaction.getProductId()).when(transactionsSplitCredits).getProductId();
        doReturn(transaction.getProductFk()).when(transactionsSplitCredits).getProductFk();
        doReturn(transaction.getUsername()).when(transactionsSplitCredits).getUsername();
        doReturn(transaction.getPortionsConsumed()).when(transactionsSplitCredits).getPortionsConsumed();
        doReturn(transaction).when(transactionsSplitCredits).asTransaction();
        doReturn(validator.handle(transactionsSplitCredits, productRepository, transactionsRepository)).when(handler).handle(transactionsSplitCredits, productRepository, transactionsRepository);

        ResponseEntity<String> result = transactionController
            .addNewTransactionSplittingCredits(transactionsSplitCredits);

        assertEquals(ResponseEntity.badRequest().body("Adding the transaction failed"), result);
    }
}
