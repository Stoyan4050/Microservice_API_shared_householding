package nl.tudelft.sem.transactions.handlers;

import org.springframework.http.ResponseEntity;


public class TokensValidator extends BaseValidator {
    @Override
    public ResponseEntity<String> handle(ValidatorHelper helper) {

        // we are sure the product is present, otherwise the product validator would have failed

        // if the credits did not change, return a 204 NO CONTENT http response
        if (helper.calculateCredits() == 0) {
            return badRequest();
        }

        return super.checkNext(helper);
    }

    public ResponseEntity<String> badRequest() {
        return ResponseEntity.noContent().build();
    }
}
