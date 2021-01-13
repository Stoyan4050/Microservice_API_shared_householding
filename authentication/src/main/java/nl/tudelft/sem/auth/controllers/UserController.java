package nl.tudelft.sem.auth.controllers;

import com.netflix.discovery.EurekaClient;
import java.util.Optional;
import javax.validation.Valid;
import nl.tudelft.sem.auth.entities.UserRegister;
import nl.tudelft.sem.auth.helpers.UserHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
     * @return A response entity depending on whether the operation was successful.
     */
    @PostMapping(value = "auth/register", consumes = {"application/json"})
    public ResponseEntity<?> register(final @Valid @RequestBody UserRegister user) {

        Optional<ResponseEntity<String>> responseEntity =  jdbcCreateUser(user);
        // if jdbcCreateUser has returned a response entity, return it, otherwise call postNewUser()
        return responseEntity.orElseGet(() -> {
            // make a POST request to the requests microservice to add the user
            UserHelper helper = new UserHelper(discoveryClient);
            return helper.postNewUser(user.getUsername(), user.getEmail());
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

}
