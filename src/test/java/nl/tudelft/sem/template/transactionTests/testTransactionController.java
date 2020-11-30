package nl.tudelft.sem.template.transactionTests;

import nl.tudelft.sem.template.transactions.server.entities.Product;
import nl.tudelft.sem.template.transactions.server.entities.Transactions;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestTransactionController {
private Product product;
private Transactions transaction = new Transactions();
private String username;
private int portionsConsumed;


@BeforeEach
public void setup() {
	product = new Product("bread", (float) 1.50, 10);
	portionsConsumed = 1;
	username = "Stoyan";
	transaction.setPortions_consumed(portionsConsumed);
	transaction.setProduct(product);
	transaction.setTransaction_id(1);
	transaction.setUsername(username);
}

@Test
public void constructorTest() {
	assertNotNull(transaction);
}

@Test
public void getPortions_consumed() {
	
	assertEquals(1, transaction.getPortions_consumed());
}

@Test
public void getProductId() {
	
	assertEquals(product.getProductId(), transaction.getProduct_id());
}
@Test
public void getUsername() {
	
	assertEquals("Stoyan", transaction.getUsername());
}
@Test
public void getProduct() {
	
	assertEquals(product.getExpired(), transaction.getProduct().getExpired());
	assertEquals(product.getPortionsLeft(), transaction.getProduct().getPortionsLeft());
	assertEquals(product.getProductName(), transaction.getProduct().getProductName());
	Assert.assertEquals(product.getPrice(),transaction.getProduct().getPrice(), 0.005f);
	assertEquals(product.getTotalPortions(), transaction.getProduct().getTotalPortions());
	assertEquals(product.getUsername(), transaction.getProduct().getUsername());
}

}
