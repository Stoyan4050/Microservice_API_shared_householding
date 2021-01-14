package nl.tudelft.sem.auth.controllers;

import com.netflix.discovery.EurekaClient;
import javax.validation.Valid;
import nl.tudelft.sem.auth.entities.UserRegister;
import nl.tudelft.sem.auth.helpers.JdbcHelper;
import nl.tudelft.sem.auth.helpers.UserHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class UserController {
    @Autowired
    private transient EurekaClient discoveryClient;

    @Autowired
    private transient JdbcHelper jdbcHelper;

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
        return jdbcHelper.checkUserExists(user.getUsername()).orElseGet(() -> {
            // if a user with that username does not exist, add it to the database
            jdbcHelper.jdbcCreateUser(user);

            // make a POST request to the requests microservice to add the user there as well
            UserHelper helper = new UserHelper(discoveryClient);
            return helper.postNewUser(user.getUsername(), user.getEmail());
        });

    }
}
