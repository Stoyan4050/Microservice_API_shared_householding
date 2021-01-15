package nl.tudelft.sem.transactions.handlers;

import nl.tudelft.sem.transactions.MicroserviceCommunicator;
import nl.tudelft.sem.transactions.entities.Product;
import org.springframework.http.ResponseEntity;

public class HouseValidator extends BaseValidator {
    @Override
	public ResponseEntity<String> handle(ValidatorHelper helper) {
        if (getProduct(helper) == null) {
            return badRequest();
        }


        try {
            int houseNumberUser = getHouseNumber(getProduct(helper).getUsername());

            int houseNumberProduct = getHouseNumber(getProduct(helper).getUsername());

            if (houseNumberProduct == -1 || houseNumberUser == -1) {
                return badRequest();
            }

            if (houseNumberProduct == houseNumberUser) {
            	return super.checkNext(helper);
            } else {
                return badRequest();
            }
			
        } catch (Exception e) {
            badRequest();
        }
		
        return badRequest();
		
    }

    public int getHouseNumber(String username) {
        return MicroserviceCommunicator.sendRequestForHouseNumber(username);
    }

    public ResponseEntity<String> badRequest() {
        return ResponseEntity.notFound().build();
    }

    public Product getProduct(ValidatorHelper helper) {
        return helper.getProduct();
    }
}
