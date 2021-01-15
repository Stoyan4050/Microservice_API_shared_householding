package nl.tudelft.sem.transactions.handlers;

import nl.tudelft.sem.transactions.MicroserviceCommunicator;
import nl.tudelft.sem.transactions.entities.Product;
import org.springframework.http.ResponseEntity;

public class HouseValidator extends BaseValidator {
    @Override
	public ResponseEntity<String> handle(ValidatorHelper helper) {
        //System.out.println("In house validator");

        if (helper.getProduct() == null) {
            return badRequest();
        }


        try {
            int houseNumberUser = getHouseNumber(helper.getTransaction().getUsername());

            int houseNumberProduct = getHouseNumber(helper.getProduct().getUsername());

            //System.out.println(houseNumberProduct);
            //System.out.println(houseNumberUser);

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

    public Product getProduct(ValidatorHelper helper) {
        return helper.getProduct();
    }

    public ResponseEntity<String> badRequest() {
        return ResponseEntity.notFound().build();
    }
}
