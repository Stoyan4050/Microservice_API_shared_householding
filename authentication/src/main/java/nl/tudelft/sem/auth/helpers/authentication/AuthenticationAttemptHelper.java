package nl.tudelft.sem.auth.helpers.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import nl.tudelft.sem.auth.entities.UserCredentials;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class AuthenticationAttemptHelper {

    static UsernamePasswordAuthenticationToken obtainCredentials(InputStream requestInputStream)
            throws IOException {
        UserCredentials user = new ObjectMapper()
                .readValue(requestInputStream, UserCredentials.class);

        return new UsernamePasswordAuthenticationToken(
                user.getUsername(), user.getPassword(), Collections.emptyList());
    }
}
