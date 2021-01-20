package nl.tudelft.sem.transactions.controllers;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.sem.transactions.entities.Product;
import nl.tudelft.sem.transactions.entities.Transactions;
import nl.tudelft.sem.transactions.entities.TransactionsSplitCredits;
import nl.tudelft.sem.transactions.handlers.ProductValidator;
import nl.tudelft.sem.transactions.handlers.TokensValidator;
import nl.tudelft.sem.transactions.handlers.TransactionValidator;
import nl.tudelft.sem.transactions.handlers.Validator;
import nl.tudelft.sem.transactions.handlers.ValidatorHelper;
import nl.tudelft.sem.transactions.repositories.ProductRepository;
import nl.tudelft.sem.transactions.repositories.TransactionsRepository;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import org.mockito.MockitoAnnotations;
import org.mockserver.integration.ClientAndServer;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.verify.VerificationTimes;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;

public class TransactionControllerTest {
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

//    private static ClientAndServer mockServer;
    private static transient ObjectMapper mapper;

    @BeforeEach
    public void setUp() {
        transactionController = spy(TransactionController.class);
        MockitoAnnotations.initMocks(this);
        transaction = new Transactions();
        transaction.setTransactionId(1L);
        transaction.setPortionsConsumed(2);
        transaction.setUsername(BOB);
        product = new Product();
        product.setProductId(4L);
        transaction.setProductFk(product);
        product.setExpired(0);
        product.setPortionsLeft(5);
        product.setTotalPortions(5);
        product.setPrice(1);
        product.setTotalPortions(5);

        validator = new ProductValidator();
        TokensValidator tokensValidator = new TokensValidator();
        TransactionValidator transactionValidator =
                // can pass it a mocked discoveryClient, when testing the microservice communication
                new TransactionValidator(null);
        tokensValidator.setNext(transactionValidator);
        validator.setNext(tokensValidator);

        helper = new ValidatorHelper(transaction, productRepository, transactionsRepository);
    }

    @BeforeAll
    public static void startServer() {
//        mockServer = startClientAndServer(9102);
        mapper = new ObjectMapper();
    }
    @AfterAll
    public static void stopServer() {
//        mockServer.stop();
        // mockServer = null;
//        mockServer.close();
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
    void editTransactionPricePerPortion() throws JsonProcessingException {
//        mockServer.reset();
        ClientAndServer mockServer = startClientAndServer(9102);
        try {
            doReturn(transaction).when(transactionsRepository).getOne(1L);
            doReturn(1).when(transactionsRepository)
                    .updateExistingTransaction(4, BOB, 2, 1L);
            doReturn(Optional.of(product)).when(productRepository)
                    .findByProductId(product.getProductId());

            float pricePerPortion = product.getPrice() / product.getTotalPortions();

            HttpRequest req = request()
                    .withMethod("POST")
                    .withPath("/editUserCredits")
                    .withQueryStringParameter("username", transaction.getUsername())
                    .withQueryStringParameter("credits", Float.toString(pricePerPortion * transaction.getPortionsConsumed()))
                    .withQueryStringParameter("add", Boolean.toString(false));

            float creditsForOldTransaction = pricePerPortion * transaction.getPortionsConsumed();
            HttpRequest req1 = request()
                    .withMethod("POST")
                    .withPath("/editUserCredits")
                    .withQueryStringParameter("username", transaction.getUsername())
                    .withQueryStringParameter("credits", Float.toString(creditsForOldTransaction))
                    .withQueryStringParameter("add", Boolean.toString(true));


            mockServer.when(req1).respond(HttpResponse.response().withHeaders().withBody(mapper.writeValueAsString(true)));
            mockServer.when(req).respond(HttpResponse.response().withHeaders().withBody(mapper.writeValueAsString(true)));

            boolean result = transactionController.editTransactions(transaction);
            product.setPortionsLeft(product.getPortionsLeft() + transaction.getPortionsConsumed());

            mockServer.verify(
                    req1,
                    VerificationTimes.exactly(1)
            );

            mockServer.verify(
                    req,
                    VerificationTimes.exactly(1)
            );

            verify(productRepository)
                    .findByProductId(transaction.getProductFk().getProductId());
            verify(transactionsRepository)
                    .updateExistingTransaction(4, BOB, 2, 1L);


            assertTrue(result);
        }
        // ensures that the mockServer will be closed even if something fails
        // required by PMD
        finally {
            mockServer.stop();
            mockServer.close();
        }

    }

    @Test
    void deleteTransaction() {
        doReturn(Optional.of(transaction)).when(transactionsRepository).findById(1L);
        doNothing().when(transactionsRepository).delete(transaction);

        ResponseEntity response = transactionController.deleteTransaction(1L);

        verify(transactionsRepository).findById(1L);
        verify(transactionsRepository).delete(transaction);

        assertEquals(ResponseEntity.ok().body("Transaction successfully deleted."), response);
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

    @Test
    void deleteTransactionFromProduct() {
        product.addTransaction(transaction);

        doReturn(Optional.of(transaction)).when(transactionsRepository).findById(1L);

        assertEquals(1, transaction.getProductFk().getTransactionsList().size());
        ResponseEntity response = transactionController.deleteTransaction(1L);

        verify(transactionsRepository).delete(transaction);
        assertEquals(0, transaction.getProductFk().getTransactionsList().size());
        assertEquals(ResponseEntity.ok().body("Transaction successfully deleted."), response);

    }

    @Test
    void deleteTransactionProductNotPresent() {
        doReturn(Optional.of(transaction)).when(transactionsRepository).findById(2L);

        ResponseEntity response = transactionController.deleteTransaction(1L);

        assertEquals(ResponseEntity.notFound().build(), response);

    }

    @Test
    void editTransactionExceptionWhenUpdateProduct() {
        product.setUsername("stoyan");
        product.setProductName("cookies");

        Transactions oldTransaction = new Transactions();
        oldTransaction.setProductFk(product);
        oldTransaction.setProduct(product);
        oldTransaction.setPortionsConsumed(2);
        oldTransaction.setTransactionId(1L);
        oldTransaction.setUsername("stoyan");

        doReturn(Optional.of(product)).when(productRepository)
                .findByProductId(product.getProductId());
        doReturn(oldTransaction).when(transactionsRepository).getOne(1L);
        doReturn(1).when(transactionsRepository)
                .updateExistingTransaction(4, BOB, 2, 1L);

        int portionsLeft = product.getPortionsLeft() + oldTransaction.getPortionsConsumed();

        doThrow(DataIntegrityViolationException.class).when(productRepository).updateExistingProduct(
                product.getProductName(),
                        product.getUsername(),
                        product.getPrice(),
                        product.getTotalPortions(),
                        (portionsLeft - transaction.getPortionsConsumed()),
                product.getExpired(),  product.getProductId());


        boolean response = transactionController.editTransactions(transaction);

        assertFalse(response);
    }

    @Test
    void editTransactionExceptionWhenUpdateTransaction() {
        doReturn(Optional.of(product)).when(productRepository)
                .findByProductId(product.getProductId());
        doReturn(0).when(transactionsRepository)
                .updateExistingTransaction(4, BOB, 2, 1L);
        doReturn(transaction).when(transactionsRepository).getOne(1L);


        boolean response = transactionController.editTransactions(transaction);

        assertFalse(response);
    }

    @Test
    void editTransactionExpiredProduct() {
        product.setExpired(1);
        doReturn(Optional.of(product)).when(productRepository)
                .findByProductId(product.getProductId());
        doReturn(transaction).when(transactionsRepository).getOne(1L);

        boolean response = transactionController.editTransactions(transaction);

        assertFalse(response);
    }

    @Test
    void editTransactionNotEnoughPortions() {
        transaction.setPortionsConsumed(10);
        Transactions oldTransaction = new Transactions();
        oldTransaction.setProductFk(product);
        oldTransaction.setProduct(product);
        oldTransaction.setPortionsConsumed(2);
        oldTransaction.setTransactionId(1L);
        oldTransaction.setUsername("stoyan");

        doReturn(Optional.of(product)).when(productRepository)
                .findByProductId(product.getProductId());
        doReturn(oldTransaction).when(transactionsRepository).getOne(1L);

        boolean response = transactionController.editTransactions(transaction);

        assertFalse(response);
    }
}
