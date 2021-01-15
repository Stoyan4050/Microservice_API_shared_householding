package nl.tudelft.sem.transactions.handlers;

import org.springframework.http.ResponseEntity;

public class ProductValidator extends BaseValidator {

    @Override
    public ResponseEntity<String> handle(ValidatorHelper helper) {

        if (helper.getProduct() == null) {
            return badRequestDoesNotExists();
        }

        int portionsLeft = calculatePortions(helper);
        if (helper.getProduct().getExpired() == 1 || portionsLeft < 0) {
            return badRequest();
        }


        return super.checkNext(helper);

    }

    public ResponseEntity<String> badRequestDoesNotExists() {
        return ResponseEntity.badRequest().body(
                "Product does not exists!");
    }

    public ResponseEntity<String> badRequest() {
        return ResponseEntity.badRequest().body(
                "Product is expired, does not exists or there are no portions left");
    }

    public int calculatePortions(ValidatorHelper helper) {
        return helper.calculatePortionsLeft();
    }
}
