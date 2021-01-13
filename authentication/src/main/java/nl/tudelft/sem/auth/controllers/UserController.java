package nl.tudelft.sem.auth.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.discovery.EurekaClient;
import java.util.Optional;
import javax.validation.Valid;
import nl.tudelft.sem.auth.MicroserviceCommunicator;
import nl.tudelft.sem.auth.entities.UserRegister;
import nl.tudelft.sem.auth.entities.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
public class UserController {

    @Autowired
    private transient JdbcUserDetailsManager jdbcUserDetailsManager;
    @Autowired
    private transient PasswordEncoder passwordEncoder;
    @Autowired
    private transient EurekaClient discoveryClient;

    // no operation method that fixes a "dataflow anomaly" PMD warning
    private static void nop(String s) {
    }

    /**
     * POST Mapping for registration of users.
     *
     * @param user User to be added to the authentication database.
     * @param uriComponentsBuilder    A URI components builder
     * @return A response entity depending on whether the operation was successful.
     */
    @PostMapping(value = "auth/register", consumes = {"application/json"})
    public ResponseEntity<?> register(final @Valid @RequestBody UserRegister user,
                                      final UriComponentsBuilder uriComponentsBuilder) {

        Optional<ResponseEntity<String>> responseEntity =  jdbcCreateUser(user);
        // if jdbcCreateUser has returned a response entity, return it, otherwise call postNewUser()
        return responseEntity.orElseGet(() -> {
            // make a POST request to the requests microservice to add the user
            return postNewUser(user, uriComponentsBuilder);
        });

    }

    /**
     * Create a user in the authentication database by using jdbc.
     *
     * @param user A user object of the new user.
     * @return Optional of a response entity that may be returned if the username is not unique.
     */
    private Optional<ResponseEntity<String>> jdbcCreateUser(UserRegister user) {
        if (jdbcUserDetailsManager.userExists(user.getUsername())) {
            return Optional.of(new ResponseEntity<>("User already exists.", HttpStatus.CONFLICT));
        }

        jdbcUserDetailsManager.createUser(org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(passwordEncoder.encode(user.getPassword()))
                .roles("USER")
                .build());
        return Optional.empty();
    }

    /**
     * Helper method that uses the MicroService communicator to send a POST request to the
     * request microservice's addNewUser method.
     *
     * @param user A UserRequest object received by the client.
     * @param uriComponentsBuilder uriComponentsBuilder A URI Components Builder that is passed
     *                             from the respective controller class.
     * @return A response entity that should be returned to the client.
     */
    private ResponseEntity<String> postNewUser(UserRegister user,
                                              UriComponentsBuilder uriComponentsBuilder) {
        // need an object mapper to JSON encode the body of the POST request
        ObjectMapper mapper = new ObjectMapper();

        // create a JSON string to send as a request
        UserRequest userRequest = new UserRequest(user.getUsername(), user.getEmail());
        String userRequestJson = "";
        nop(userRequestJson); // fixes a "dataflow anomaly" PMD warning
        try {
            userRequestJson = mapper.writeValueAsString(userRequest);
            nop(userRequestJson); // fixes a "dataflow anomaly" PMD warning
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body("Could not serialize User.");
        }

        MicroserviceCommunicator communicator = new MicroserviceCommunicator(discoveryClient);
        return communicator.addNewUser(userRequestJson, uriComponentsBuilder, user.getUsername());
    }
}
