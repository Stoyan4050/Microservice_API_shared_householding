package nl.tudelft.sem.transactions.handlers;

import java.util.Optional;
import nl.tudelft.sem.transactions.MicroserviceCommunicator;
import nl.tudelft.sem.transactions.entities.Product;
import nl.tudelft.sem.transactions.entities.Transactions;
import nl.tudelft.sem.transactions.repositories.ProductRepository;
import nl.tudelft.sem.transactions.repositories.TransactionsRepository;
import org.springframework.http.ResponseEntity;

public class HouseValidator extends BaseValidator {
    @Override
	public ResponseEntity<String> handle(Transactions transaction,
					ProductRepository productRepository,
					TransactionsRepository transactionsRepository) {
        System.out.println("In house validator");
        Optional<Product> transactionProduct =  productRepository
							.findById(transaction.getProductId());

        Product product;
        if (transactionProduct.isPresent()) {
            product = transactionProduct.get();
        } else {
            return ResponseEntity.notFound().build();
        }
		
		
        try {
            int houseNumberUser = MicroserviceCommunicator.sendRequestForHouseNumber(
					transaction.getUsername());
			
            int houseNumberProduct = MicroserviceCommunicator.sendRequestForHouseNumber(
					product.getUsername());
			
            System.out.println(houseNumberProduct);
            System.out.println(houseNumberUser);

            if (houseNumberProduct == -1 || houseNumberUser == -1) {
                return ResponseEntity.notFound().build();
            }

            if (houseNumberProduct == houseNumberUser) {
            	return super.checkNext(transaction, productRepository, transactionsRepository);
            } else {
                return ResponseEntity.notFound().build();
            }
			
        } catch (Exception e) {
            e.printStackTrace();
        }
		
        return ResponseEntity.notFound().build();
		
    }
}
