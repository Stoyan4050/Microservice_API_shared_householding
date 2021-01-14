package nl.tudelft.sem.transactions.handlers;

import java.util.Optional;
import nl.tudelft.sem.transactions.MicroserviceCommunicator;
import nl.tudelft.sem.transactions.entities.Product;
import org.springframework.http.ResponseEntity;

public class HouseValidator extends BaseValidator {
    @Override
	public ResponseEntity<String> handle(ValidatorHelper helper) {
        System.out.println("In house validator");
        Optional<Product> transactionProduct =  helper.getProductRepository()
							.findById(helper
                                    .getTransaction()
                                    .getProductId());

        Product product;
        if (transactionProduct.isPresent()) {
            product = transactionProduct.get();
        } else {
            return ResponseEntity.notFound().build();
        }


        try {
            int houseNumberUser = MicroserviceCommunicator.sendRequestForHouseNumber(
					helper.getTransaction().getUsername());

            int houseNumberProduct = MicroserviceCommunicator.sendRequestForHouseNumber(
					product.getUsername());

            System.out.println(houseNumberProduct);
            System.out.println(houseNumberUser);

            if (houseNumberProduct == -1 || houseNumberUser == -1) {
                return ResponseEntity.notFound().build();
            }

            if (houseNumberProduct == houseNumberUser) {
            	return super.checkNext(helper);
            } else {
                return ResponseEntity.notFound().build();
            }
			
        } catch (Exception e) {
            e.printStackTrace();
        }
		
        return ResponseEntity.notFound().build();
		
    }
}
