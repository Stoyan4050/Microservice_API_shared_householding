package nl.tudelft.sem.auth.helpers;

import java.util.Optional;
import nl.tudelft.sem.auth.entities.UserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Component;

@Component
public class JdbcHelper {
    @Autowired
    private transient JdbcUserDetailsManager jdbcUserDetailsManager;

    @Autowired
    private transient UserBuilderHelper userBuilderHelper;

    /**
     * Check whether a user with a given username already exists in the database.
     *
     * @param username The username that should be checked whether it's unique.
     * @return An Optional of a ResponseEntity if the username is already in use.
     *         An empty Optional otherwise.
     */
    public Optional<ResponseEntity<String>> checkUserExists(String username) {
        if (this.jdbcUserDetailsManager.userExists(username)) {
            return Optional.of(new ResponseEntity<>("User already exists.", HttpStatus.CONFLICT));
        }
        return Optional.empty();
    }

    /**
     * Create a user in the authentication database by using jdbc.
     *
     * @param user A user object of the new user.
     */
    public void jdbcCreateUser(UserRegister user) {
        this.jdbcUserDetailsManager
                .createUser(userBuilderHelper.buildUser(user.getUsername(), user.getPassword()));
    }


}
