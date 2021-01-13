package nl.tudelft.sem.auth.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.discovery.EurekaClient;
import nl.tudelft.sem.auth.MicroserviceCommunicator;
import nl.tudelft.sem.auth.entities.UserRequest;
import org.springframework.http.ResponseEntity;

public class UserRequestHelper {
    // need an object mapper to JSON encode the body of the POST request
    private transient ObjectMapper mapper = new ObjectMapper();

    private transient MicroserviceCommunicator microserviceCommunicator;

    public UserRequestHelper(EurekaClient discoveryClient) {
        this.microserviceCommunicator = new MicroserviceCommunicator(discoveryClient);
    }

    /**
     * JSON serialize a UserRequest object using this.mapper.
     *
     * @param username The username of the UserRequest object that is to be serialized.
     * @param email The email of the UserRequest object that is to be serialized.
     * @return A JSON string representing the serialized UserRequest.
     * @throws JsonProcessingException May be thrown by the ObjectMapper's
     *                                 writeValueAsString() method.
     */
    String serializeUserRequest(String username,
                                        String email) throws JsonProcessingException {
        return this.mapper.writeValueAsString(new UserRequest(username, email));
    }

    /**
     * Add a new user to the request microservice's database using the MicroserviceCommunicator.
     *
     * @param userRequestJson A JSON representation of the UserRequest object.
     * @param username The username of the user, whose UserRequest should be added
     *                 to the request microservice's database.
     * @return A ResponseEntity depending on whether the operation was successful.
     */
    ResponseEntity<String> addUser(String userRequestJson, String username) {
        return this.microserviceCommunicator.addNewUser(userRequestJson, username);
    }
}
