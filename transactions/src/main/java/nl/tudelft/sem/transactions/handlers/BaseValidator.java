package nl.tudelft.sem.transactions.handlers;

import nl.tudelft.sem.transactions.MicroserviceCommunicator;
import org.springframework.http.ResponseEntity;

public abstract class BaseValidator implements Validator {
    protected transient Validator next;

    public Validator setNext(Validator v) {
        this.next = v;
        return v;
    }

    protected ResponseEntity<String> checkNext(ValidatorHelper helper) {
        if (next == null) {
            return ResponseEntity.ok().body("Transaction was successfully added");
        }
        return next.handle(helper);
    }

    public int calculatePortionsLeft(ValidatorHelper helper) {
        return helper.getProduct().getPortionsLeft()
                - helper.getPortionsConsumed();
    }

    public int getHouseNumber(String username) {
        return MicroserviceCommunicator.sendRequestForHouseNumber(username);
    }


}
