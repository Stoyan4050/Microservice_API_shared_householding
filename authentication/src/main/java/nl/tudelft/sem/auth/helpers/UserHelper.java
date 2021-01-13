package nl.tudelft.sem.auth.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.discovery.EurekaClient;
import nl.tudelft.sem.auth.MicroserviceCommunicator;
import nl.tudelft.sem.auth.entities.UserRegister;
import nl.tudelft.sem.auth.entities.UserRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class UserHelper {
    transient ObjectMapper mapper;
    transient MicroserviceCommunicator microserviceCommunicator;

    /**
     * Creates a new UserHelper.
     *
     * @param discoveryClient Eureka client that can be used
     *                        to get the URI of the other microservices.
     */
    public UserHelper(EurekaClient discoveryClient) {
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
     * @param username The username of the user as received from the client.
     * @param email The email of the user as received from the client.
     * @return A response entity that should be returned to the client.
     */
    public ResponseEntity<String> postNewUser(String username, String email) {
        // create a JSON string to send as a request
        String userRequestJson = "";
        nop(userRequestJson); // fixes a "dataflow anomaly" PMD warning
        try {
            userRequestJson = serializeUserRequest(username, email);
            nop(userRequestJson); // fixes a "dataflow anomaly" PMD warning
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body("Could not serialize User.");
        }
        return addUser(userRequestJson, username);
    }

    private String serializeUserRequest(String username,
                                        String email) throws JsonProcessingException {
        return this.mapper.writeValueAsString(new UserRequest(username, email));
    }

    private ResponseEntity<String> addUser(String userRequestJson, String username) {
        return this.microserviceCommunicator.addNewUser(userRequestJson, username);
    }

}
