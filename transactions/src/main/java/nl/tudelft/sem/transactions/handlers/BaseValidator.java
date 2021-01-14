package nl.tudelft.sem.transactions.handlers;

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

}
