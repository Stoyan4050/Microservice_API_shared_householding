package nl.tudelft.sem.auth.config;

import java.io.IOException;
import java.util.Date;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nl.tudelft.sem.auth.helpers.authentication.AuthenticationHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class JwtFilter extends UsernamePasswordAuthenticationFilter {

    private final transient AuthenticationManager authManager;

    private final transient JwtConf jwtConfig;

    /**
     * Creates an instance of a JwtFilter.
     *
     * @param authManager An authentication manager
     * @param jwtConfig   A config for the JWT token defined in a separate class
     */
    public JwtFilter(AuthenticationManager authManager,
                     JwtConf jwtConfig) {
        this.authManager = authManager;
        this.jwtConfig = jwtConfig;

        // redirect auth/login to jwt filter
        this.setRequiresAuthenticationRequestMatcher(
            new AntPathRequestMatcher("/auth/login", "POST")
        );
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) {
        try {
            // Obtain credentials
            return authManager.authenticate(AuthenticationHelper
                    .obtainCredentials(request.getInputStream()));

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain,
        Authentication auth
    ) {
        long now = System.currentTimeMillis();
        Date current = new Date(now);
        Date expiration = new Date(now + jwtConfig.getExpiration() * 1000L);
        byte[] bytes = jwtConfig.getSecret().getBytes();
        String token = AuthenticationHelper.generateToken(auth, current, expiration, bytes);
        response.addHeader(jwtConfig.getHeader(), jwtConfig.getPrefix() + token);
    }

}