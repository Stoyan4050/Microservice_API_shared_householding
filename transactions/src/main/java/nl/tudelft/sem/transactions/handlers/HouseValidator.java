package nl.tudelft.sem.transactions.handlers;

import org.springframework.http.ResponseEntity;

public class HouseValidator extends BaseValidator {
    @Override
	public ResponseEntity<String> handle(ValidatorHelper helper) {

        try {
            int houseNumberUser = getHouseNumber(getTransactionUsername(helper));

            int houseNumberProduct = getHouseNumber(getProductUsername(helper));

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

    public ResponseEntity<String> badRequest() {
        return ResponseEntity.notFound().build();
    }

    public String getProductUsername(ValidatorHelper helper) {
        return helper.getProduct().getUsername();
    }

    public String getTransactionUsername(ValidatorHelper helper) {
        return helper.getTransaction().getUsername();
    }

}
