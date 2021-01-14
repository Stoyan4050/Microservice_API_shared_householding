package nl.tudelft.sem.auth.helpers.authentication;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class AuthenticationSuccessHelper {
    static String generateJwtToken(Authentication auth, Date now, Date expiration, byte[] bytes) {
        return createJwtBuilder(auth)
                .setIssuedAt(now)
                .setExpiration(expiration)  // in milliseconds
                .signWith(SignatureAlgorithm.HS512, bytes)
                .compact();
    }

    private static JwtBuilder createJwtBuilder(Authentication auth) {
        return Jwts.builder()
                .setSubject(auth.getName())
                .claim("authorities", auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
    }
}
