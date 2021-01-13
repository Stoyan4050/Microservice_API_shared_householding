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

        // if jdbcCreateUser has returned a response entity, return it, otherwise call postNewUser()
        return checkUserExists(user.getUsername()).orElseGet(() -> {
            // if a user with that username does not exist, add it to the database
            jdbcCreateUser(user);

            // make a POST request to the requests microservice to add the user there as well
            UserHelper helper = new UserHelper(discoveryClient);
            return helper.postNewUser(user.getUsername(), user.getEmail());
        });

    }

    /**
     * Check whether a user with a given username already exists in the database.
     *
     * @param username The username that should be checked whether it's unique.
     * @return An Optional of a ResponseEntity if the username is already in use.
     *         An empty Optional otherwise.
     */
    private Optional<ResponseEntity<String>> checkUserExists(String username) {
        if (jdbcUserDetailsManager.userExists(username)) {
            return Optional.of(new ResponseEntity<>("User already exists.", HttpStatus.CONFLICT));
        }
        return Optional.empty();
    }

    /**
     * Create a user in the authentication database by using jdbc.
     *
     * @param user A user object of the new user.
     */
    private void jdbcCreateUser(UserRegister user) {
        jdbcUserDetailsManager.createUser(buildUser(user.getUsername(), user.getPassword()));
    }

    /**
     * Build a UserDetails object that can be used by jdbcUserDetailsManager
     * to add new users to the database.
     *
     * @param username Username of the user that will be used to build a UserDetails object.
     * @param password Password of the user that will be used to build a UserDetails object.
     * @return The built UserDetails object.
     */
    private org.springframework.security.core.userdetails.UserDetails buildUser(String username,
                                                                                String password) {
        return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(encodePassword(password))
                .roles("USER")
                .build();
    }

    /**
     * Helper method that uses this.passwordEncoder to encode a given password.
     *
     * @param password Password to be encoded.
     * @return The encoded password as a String.
     */
    private String encodePassword(String password) {
        return this.passwordEncoder.encode(password);
    }
}
