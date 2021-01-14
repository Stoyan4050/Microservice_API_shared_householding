package nl.tudelft.sem.transactions.handlers;

import com.netflix.discovery.EurekaClient;
import nl.tudelft.sem.transactions.MicroserviceCommunicator;
import org.springframework.http.ResponseEntity;

public class TransactionValidator extends BaseValidator {
    private transient EurekaClient discoveryClient;
    private transient String username;

    public TransactionValidator(EurekaClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }


    @Override
    public ResponseEntity<String> handle(ValidatorHelper helper) {

        // we are sure the product is present, otherwise the product validator would have failed
        TransactionCommunicator communicator = new TransactionCommunicator(helper);

        if (communicator.changeCredits()) {
            return this.checkNext(helper);
        } else {
            return ResponseEntity.badRequest().body("Adding the transaction failed");
        }
    }

    @Override
    protected ResponseEntity<String> checkNext(ValidatorHelper helper) {
        if (next == null) {
            // if the discovery client is not autowired properly,
            // don't bother trying to reach the requests microservice
            if (discoveryClient == null) {
                return ResponseEntity.ok().body("Transaction was successfully added");
            }
            return ResponseEntity.ok()
                    .body("Transaction was successfully added. Remaining credits for "
                            + username + ": "
                            + MicroserviceCommunicator.getCredits(username, discoveryClient));
        }
        return next.handle(helper);
    }
}
