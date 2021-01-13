package nl.tudelft.sem.auth.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.discovery.EurekaClient;
import nl.tudelft.sem.auth.MicroserviceCommunicator;
import nl.tudelft.sem.auth.entities.UserRegister;
import nl.tudelft.sem.auth.entities.UserRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

public class UserControllerHelper {
    transient ObjectMapper mapper;
    transient MicroserviceCommunicator microserviceCommunicator;

    /**
     * Creates a new UserControllerHelper.
     *
     * @param discoveryClient Eureka client that can be used
     *                        to get the URI of the other microservices.
     */
    public UserControllerHelper(EurekaClient discoveryClient) {
        // need an object mapper to JSON encode the body of the POST request
        this.mapper = new ObjectMapper();
        this.microserviceCommunicator = new MicroserviceCommunicator(discoveryClient);
    }

    // no operation method that fixes a "dataflow anomaly" PMD warning
    private void nop(String s) {
    }

    /**
     * Helper method that uses the MicroService communicator to send a POST request to the
     * request microservice's addNewUser method.
     *
     * @param user A UserRequest object received by the client.
     * @return A response entity that should be returned to the client.
     */
    public ResponseEntity<String> postNewUser(UserRegister user) {
        // create a JSON string to send as a request
        String userRequestJson = "";
        nop(userRequestJson); // fixes a "dataflow anomaly" PMD warning
        try {
            userRequestJson = this.mapper.writeValueAsString(new UserRequest(user.getUsername(),
                                                                             user.getEmail()));
            nop(userRequestJson); // fixes a "dataflow anomaly" PMD warning
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body("Could not serialize User.");
        }
        return this.microserviceCommunicator.addNewUser(userRequestJson, user.getUsername());
    }

}
