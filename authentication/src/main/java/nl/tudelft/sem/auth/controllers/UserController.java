package nl.tudelft.sem.auth.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javax.validation.Valid;
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
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
public class UserController {

    @Autowired
    private transient JdbcUserDetailsManager jdbcUserDetailsManager;
    @Autowired
    private transient PasswordEncoder passwordEncoder;

    @Autowired
    private transient EurekaClient discoveryClient;

    /**
     * POST Mapping for registration of users.
     *
     * @param user User to be added to the authentication database.
     * @param b    A URI components builder
     * @return A response entity depending on whether the operation was successful.
     */
    @PostMapping(value = "auth/register", consumes = {"application/json"})
    public ResponseEntity<?> register(final @Valid @RequestBody UserRegister user,
                                      final UriComponentsBuilder b) {

        final String username = user.getUsername();
        if (jdbcUserDetailsManager.userExists(username)) {
            return new ResponseEntity<>("User already exists.", HttpStatus.CONFLICT);
        }

        jdbcUserDetailsManager.createUser(org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(passwordEncoder.encode(user.getPassword()))
            .roles("USER")
            .build());

        // make a POST request to the requests microservice to add the user

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

        // get the uri of the requests microservice from eureka
        InstanceInfo requestsInstance = discoveryClient.getNextServerFromEureka("REQUESTS", false);
        String requestsUri = requestsInstance.getHomePageUrl();

        // build a new POST request
        HttpRequest addNewUserReq = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(userRequestJson))
                .uri(URI.create(requestsUri + "addNewUser/")).build();

        HttpResponse<String> response = null;
        HttpClient client = HttpClient.newBuilder().build();
        try {
            response = client.send(addNewUserReq, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        // if the microservice returns anything different from 201, report to the client
        if (response.statusCode() != HttpStatus.CREATED.value()) {
            System.out.println("Status: " + response.statusCode());
            return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY)
                    .body("Requests microservice returned: "
                            + HttpStatus.resolve(response.statusCode()));
        }

        final UriComponents uri = b.path("register/{user_name}").buildAndExpand(username);
        return ResponseEntity.created(uri.toUri()).build();
    }

    // no operation method that fixes a "dataflow anomaly" PMD warning
    private static void nop(String s) {}
}
