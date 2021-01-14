package nl.tudelft.sem.auth.helpers.authentication;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

public class AuthenticationHelper {
    public static String generateToken(Authentication auth,
                                       Date current,
                                       Date expiration,
                                       byte[] bytes) {
        return AuthenticationSuccessHelper.generateJwtToken(auth, current, expiration, bytes);
    }

    public static UsernamePasswordAuthenticationToken
        obtainCredentials(InputStream requestInputStream) throws IOException {

        return AuthenticationAttemptHelper.obtainCredentials(requestInputStream);
    }
}
