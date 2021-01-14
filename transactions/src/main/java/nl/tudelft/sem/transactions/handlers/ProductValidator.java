package nl.tudelft.sem.transactions.handlers;

import java.util.Optional;
import nl.tudelft.sem.transactions.entities.Product;
import org.springframework.http.ResponseEntity;

public class ProductValidator extends BaseValidator {

    @Override
    public ResponseEntity<String> handle(ValidatorHelper helper) {
        Optional<Product> optionalProduct = helper.getProductRepository().findByProductId(
                helper.getTransaction().getProductId());

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            int portionsLeft = product.getPortionsLeft()
                    - helper.getTransaction().getPortionsConsumed();

            if (product.getExpired() == 1 || portionsLeft < 0) {
                return ResponseEntity.badRequest().body(
                        "Product is expired or there is no portions left");
            }

            return super.checkNext(helper);

        }
        return ResponseEntity.notFound().build();
    }
}
