package nl.tudelft.sem.transactions.controllers;

import java.util.List;
import java.util.Optional;

import nl.tudelft.sem.transactions.MicroserviceCommunicator;
import nl.tudelft.sem.transactions.entities.Product;
import nl.tudelft.sem.transactions.entities.Transactions;
import nl.tudelft.sem.transactions.entities.TransactionsSplitCredits;
import nl.tudelft.sem.transactions.handlers.*;
import nl.tudelft.sem.transactions.repositories.ProductRepository;
import nl.tudelft.sem.transactions.repositories.TransactionsRepository;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;

import org.mockito.MockitoAnnotations;
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
    private transient ValidatorHelper helper;

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
        product.setPrice(1);

        validator = new ProductValidator();
        TokensValidator tokensValidator = new TokensValidator();
        TransactionValidator transactionValidator =
                // can pass it a mocked discoveryClient, when testing the microservice communication
                new TransactionValidator(null);
        tokensValidator.setNext(transactionValidator);
        validator.setNext(tokensValidator);

        helper = new ValidatorHelper(transaction, productRepository, transactionsRepository);
    }

    @Test
    void editTransaction() {
        doReturn(transaction).when(transactionsRepository).getOne(1L);
        doReturn(1).when(transactionsRepository)
                .updateExistingTransaction(4, BOB, 2, 1L);
        doReturn(Optional.of(product)).when(productRepository)
                .findByProductId(product.getProductId());

        boolean result = transactionController.editTransactions(transaction);

        verify(transactionsRepository)
                .updateExistingTransaction(4, BOB, 2, 1L);
        assertTrue(result);
    }

    @Test
    void editTransactionNoneProduct() {
        doReturn(transaction).when(transactionsRepository).getOne(1L);
        doReturn(1).when(transactionsRepository)
                .updateExistingTransaction(4, BOB, 2, 1L);

        boolean result = transactionController.editTransactions(transaction);

        verify(productRepository)
                .findByProductId(transaction.getProductFk().getProductId());
        assertFalse(result);
    }

    @Test
    void editTransactionPricePerPortion() {
        ClientAndServer mockServer = startClientAndServer(9102);
        // for some reason PMD requires the mockServer.close(); to be in a
        // try-finally block
        try {
            doReturn(transaction).when(transactionsRepository).getOne(1L);
            doReturn(1).when(transactionsRepository)
                    .updateExistingTransaction(4, BOB, 2, 1L);
            doReturn(Optional.of(product)).when(productRepository)
                    .findByProductId(product.getProductId());

            boolean result = transactionController.editTransactions(transaction);

            float pricePerPortion = product.getPrice() / product.getTotalPortions();
            product.setPortionsLeft(product.getPortionsLeft() + transaction.getPortionsConsumed());

            mockServer.verify(
                    request()
                            .withMethod("POST")
                            .withPath("/editUserCredits")
                            .withPathParameter("username", transaction.getUsername())
                            .withPathParameter("credits", Float.toString(pricePerPortion * transaction.getPortionsConsumed()))
                            .withPathParameter("add", Boolean.toString(false))
            );


            verify(productRepository)
                    .findByProductId(transaction.getProductFk().getProductId());
            verify(transactionsRepository)
                    .updateExistingTransaction(4, BOB, 2, 1L);
            assertTrue(result);
        }
        finally{
            mockServer.close();
            mockServer.stop();
        }
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
        doReturn(Optional.of(product)).when(productRepository).findById(4L);
        doReturn(validator.handle(helper))
                .when(handler).handle(new ValidatorHelper(transaction, productRepository, transactionsRepository));

        ResponseEntity<String> result = transactionController.addNewTransaction(transaction);

        verify(transactionsRepository).save(transaction);

        assertEquals(ResponseEntity.ok().body("Transaction was successfully added"),
                result);
    }

    @Test
    void addNewTransactionProductNotPresent() {
        doReturn(Optional.empty()).when(productRepository).findById(4L);
        doReturn(validator.handle(helper))
                .when(handler).handle(new ValidatorHelper(transaction, productRepository, this.transactionsRepository));

        ResponseEntity<String> result = transactionController.addNewTransaction(transaction);

        assertEquals(ResponseEntity.badRequest().body(
                "Product does not exists!"),
                result);
    }

    @Test
    void addNewTransactionFalse() {
        doReturn(Optional.of(product)).when(productRepository).findById(4L);

        doThrow(DataIntegrityViolationException.class).when(transactionsRepository)
                .save(transaction);
        doReturn(validator.handle(helper))
                .when(handler).handle(new ValidatorHelper(transaction, productRepository, transactionsRepository));

        ResponseEntity<String> result = transactionController.addNewTransaction(transaction);

        assertEquals(ResponseEntity.badRequest().body("Adding the transaction failed"), result);
    }

    @Test
    void addNewTransactionSplittingCredits() {
        doReturn(Optional.of(product)).when(productRepository).findById(4L);

        // mock TransactionsSplitCredits
        doReturn(List.of(BOB)).when(this.transactionsSplitCredits).getUsernames();
        doReturn(transaction.getTransactionId()).when(transactionsSplitCredits).getTransactionId();
        doReturn(transaction.getProductId()).when(transactionsSplitCredits).getProductId();
        doReturn(transaction.getProductFk()).when(transactionsSplitCredits).getProductFk();
        doReturn(transaction.getUsername()).when(transactionsSplitCredits).getUsername();
        doReturn(transaction.getPortionsConsumed()).when(transactionsSplitCredits)
                .getPortionsConsumed();
        doReturn(transaction).when(transactionsSplitCredits).asTransaction();

        doReturn(validator
                .handle(helper))
                .when(handler)
                .handle(new ValidatorHelper(transactionsSplitCredits, productRepository, transactionsRepository));

        ResponseEntity<String> result = transactionController
                .addNewTransactionSplittingCredits(transactionsSplitCredits);

        verify(transactionsRepository).save(transaction);

        assertEquals(ResponseEntity.ok().body("Transaction was successfully added"),
                result);
    }

    @Test
    void addNewTransactionSplittingCreditsNullProduct() {
        doReturn(Optional.empty()).when(this.productRepository).findById(4L);
        doReturn(validator
                .handle(new ValidatorHelper(transactionsSplitCredits, productRepository, transactionsRepository)))
                .when(handler)
                .handle(new ValidatorHelper(transactionsSplitCredits, productRepository, transactionsRepository));

        ResponseEntity<String> result = transactionController
                .addNewTransactionSplittingCredits(transactionsSplitCredits);
        assertEquals(ResponseEntity.badRequest().body(
                "Product does not exists!"), result);
    }

    @Test
    void addNewTransactionSplittingCreditsExpiredProduct() {
        transaction.getProductFk().setExpired(1);
        doReturn(Optional.of(product)).when(productRepository).findById(4L);

        // mock TransactionsSplitCredits
        doReturn(transaction.getTransactionId()).when(transactionsSplitCredits).getTransactionId();
        doReturn(transaction.getProductId()).when(transactionsSplitCredits).getProductId();
        doReturn(transaction.getProductFk()).when(transactionsSplitCredits).getProductFk();
        doReturn(transaction.getUsername()).when(transactionsSplitCredits).getUsername();
        doReturn(transaction.getPortionsConsumed()).when(transactionsSplitCredits)
                .getPortionsConsumed();
        doReturn(transaction).when(transactionsSplitCredits).asTransaction();
        doReturn(validator
                .handle(new ValidatorHelper(transaction, productRepository, transactionsRepository)))
                .when(handler)
                .handle(new ValidatorHelper(transaction, productRepository, transactionsRepository));

        ResponseEntity<String> result = transactionController
                .addNewTransactionSplittingCredits(transactionsSplitCredits);
        assertEquals(ResponseEntity.badRequest().body(
                "Product is expired, does not exists or there are no portions left"), result);
    }

    @Test
    void addNewTransactionSplittingCreditsNoPortionsLeft() {
        transaction.getProductFk().setPortionsLeft(-1);

        doReturn(Optional.of(product)).when(productRepository).findById(4L);

        // mock TransactionsSplitCredits
        doReturn(transaction.getTransactionId()).when(transactionsSplitCredits).getTransactionId();
        doReturn(transaction.getProductId()).when(transactionsSplitCredits).getProductId();
        doReturn(transaction.getProductFk()).when(transactionsSplitCredits).getProductFk();
        doReturn(transaction.getUsername()).when(transactionsSplitCredits).getUsername();
        doReturn(transaction.getPortionsConsumed()).when(transactionsSplitCredits)
                .getPortionsConsumed();
        doReturn(transaction).when(transactionsSplitCredits).asTransaction();
        doReturn(validator
                .handle(new ValidatorHelper(transaction, productRepository, transactionsRepository)))
                .when(handler)
                .handle(new ValidatorHelper(transaction, productRepository, transactionsRepository));

        ResponseEntity<String> result = transactionController
                .addNewTransactionSplittingCredits(transactionsSplitCredits);
        assertEquals(ResponseEntity.badRequest().body(
                "Product is expired, does not exists or there are no portions left"), result);
    }

    @Test
    void addNewTransactionSplittingCreditsDataIntegrityViolation() {
        doReturn(Optional.of(product)).when(productRepository).findById(4L);
        doReturn(List.of(BOB)).when(transactionsSplitCredits).getUsernames();

        doThrow(DataIntegrityViolationException.class).when(transactionsRepository)
                .save(transaction);

        // mock TransactionsSplitCredits should be last
        doReturn(transaction.getTransactionId()).when(transactionsSplitCredits).getTransactionId();
        doReturn(transaction.getProductId()).when(transactionsSplitCredits).getProductId();
        doReturn(transaction.getProductFk()).when(transactionsSplitCredits).getProductFk();
        doReturn(transaction.getUsername()).when(transactionsSplitCredits).getUsername();
        doReturn(transaction.getPortionsConsumed()).when(transactionsSplitCredits)
                .getPortionsConsumed();
        doReturn(validator
                .handle(new ValidatorHelper(transaction, productRepository, transactionsRepository)))
                .when(handler)
                .handle(new ValidatorHelper(transaction, productRepository, transactionsRepository));

        doReturn(transaction).when(transactionsSplitCredits).asTransaction();

        ResponseEntity<String> result = transactionController
                .addNewTransactionSplittingCredits(transactionsSplitCredits);

        assertEquals(ResponseEntity.badRequest().body("Adding the transaction failed"), result);
    }
}
