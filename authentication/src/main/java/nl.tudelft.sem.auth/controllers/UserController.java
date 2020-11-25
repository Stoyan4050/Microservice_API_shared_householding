package nl.tudelft.sem.auth.controllers;

import javax.validation.Valid;
import nl.tudelft.sem.auth.entities.User;
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
    JdbcUserDetailsManager jdbcUserDetailsManager;

    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * POST Mapping for registration of users.

     * @param user User to be added to the authentication database.
     * @param b A URI components builder

     * @return A response entity depending on whether the operation was successful.
     */
    @PostMapping(value = "register", consumes = {"application/json"})
    public ResponseEntity<?> register(@Valid @RequestBody User user, UriComponentsBuilder b) {

        String username = user.getUsername();
        if (jdbcUserDetailsManager.userExists(username)) {
            return new ResponseEntity<>("User already exists.", HttpStatus.CONFLICT);
        }

        UriComponents uri = b.path("register/{user_name}").buildAndExpand(username);

        jdbcUserDetailsManager.createUser(org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(passwordEncoder.encode(user.getPassword())).build());

        return ResponseEntity.created(uri.toUri()).build();
    }
}
