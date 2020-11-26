package nl.tudelft.sem.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nl.tudelft.sem.auth.entities.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@SuppressWarnings("PMD")
public class JwtFilter extends UsernamePasswordAuthenticationFilter {

    @SuppressWarnings("PMD")
    private AuthenticationManager authManager;

    private final JwtConf jwtConfig;

    /**
     * Creates an instance of a of a JwtFilter.

     * @param authManager An authentication manager
     * @param jwtConfig A config for the JWT token defined in a separate class
     */
    @SuppressWarnings("PMD")
    public JwtFilter(AuthenticationManager authManager,
                     JwtConf jwtConfig) {
        this.authManager = authManager;
        this.jwtConfig = jwtConfig;

        // redirect auth/login to jwt filter
        this.setRequiresAuthenticationRequestMatcher(
                new AntPathRequestMatcher("/auth/login", "POST")
        );
    }

    @SuppressWarnings("PMD")
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) {
        try {
            // Obtain credentials
            User user = new ObjectMapper().readValue(request.getInputStream(), User.class);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    user.getUsername(), user.getPassword(), Collections.emptyList());
            return authManager.authenticate(authToken);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("PMD")
    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication auth
    ) throws IOException, ServletException {
        Long now = System.currentTimeMillis();
        String token = Jwts.builder()
                .setSubject(auth.getName())
                .claim("authorities", auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + jwtConfig.getExpiration() * 1000))  // in milliseconds
                .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret().getBytes())
                .compact();
        response.addHeader(jwtConfig.getHeader(), jwtConfig.getPrefix() + token);
    }

}