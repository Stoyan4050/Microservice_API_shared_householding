package nl.tudelft.sem.transactions.handlers;

import com.netflix.discovery.EurekaClient;
import nl.tudelft.sem.transactions.MicroserviceCommunicator;
import nl.tudelft.sem.transactions.entities.Product;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;

public class TransactionValidator extends BaseValidator {
    private final transient EurekaClient discoveryClient;

    public TransactionValidator(EurekaClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }


    @Override
    public ResponseEntity<String> handle(ValidatorHelper helper) {

        TransactionCommunicator communicator = new TransactionCommunicator(helper);

        communicator.requestForTransaction(helper.calculateCredits(getProduct(helper)));
        try {

            communicator.saveTransaction();
            communicator.updateProduct(getProduct(helper),
                    helper.calculatePortionsLeft());

        } catch (DataIntegrityViolationException e) {
            return badRequest();
        }
        return this.checkNext(helper);
    }

    @Override
    protected ResponseEntity<String> checkNext(ValidatorHelper helper) {
        if (next == null) {
            // if the discovery client is not autowired properly,
            // don't bother trying to reach the requests microservice
            if (discoveryClient == null) {
                return goodRequest("Transaction was successfully added");
            }
            return goodRequest("Transaction was successfully added. Remaining credits for "
                            + getUsername(helper) + ": "
                            + getCredits(helper));
        }
        return next.handle(helper);

    }

    public ResponseEntity<String> badRequest() {
        return ResponseEntity.badRequest().body("Adding the transaction failed");
    }

    public float getCredits(ValidatorHelper helper) {
        return MicroserviceCommunicator.getCredits(getUsername(helper), discoveryClient);
    }

    public ResponseEntity<String> goodRequest(String body) {
        return ResponseEntity.ok().body(body);
    }

    public Product getProduct(ValidatorHelper helper) {
        return helper.getProduct();
    }

    public String getUsername(ValidatorHelper helper) {
        return helper.getTransaction().getUsername();
    }

}
