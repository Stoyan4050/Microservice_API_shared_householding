package nl.tudelft.sem.transactions.handlers;

import nl.tudelft.sem.transactions.entities.Product;
import org.springframework.http.ResponseEntity;


public class TokensValidator extends BaseValidator {
    @Override
    public ResponseEntity<String> handle(ValidatorHelper helper) {

        // we are sure the product is present, otherwise the product validator would have failed
        Product product = helper.getProductRepository().findByProductId(
                helper.getTransaction().getProductId()).get();

        float credits = helper.calculateCredits(product);

        // if the credits did not change, return a 204 NO CONTENT http response
        if (credits == 0) {
            return ResponseEntity.noContent().build();
        }

        return super.checkNext(helper);
    }
}
