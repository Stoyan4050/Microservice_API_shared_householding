package nl.tudelft.sem.transactions.handlers;

import org.springframework.http.ResponseEntity;

public interface Validator {
    Validator setNext(Validator handler);

    ResponseEntity<String> handle(ValidatorHelper helper);
}
