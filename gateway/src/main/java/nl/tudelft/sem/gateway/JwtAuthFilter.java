package nl.tudelft.sem.gateway;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@SuppressWarnings("PMD")
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtConf jwtConf;

    @SuppressWarnings("PMD")
    public JwtAuthFilter(JwtConf jwtConf) {
        this.jwtConf = jwtConf;
    }

    @SuppressWarnings("PMD")
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
        throws ServletException, IOException {

        // auth header
        String header = request.getHeader(jwtConf.getHeader());

        // if the header is not valid, go to the next filter in the filter chain.
        if (header == null || !header.startsWith(jwtConf.getPrefix())) {
            chain.doFilter(request, response);
            return;
        }
        String token = header.replace(jwtConf.getPrefix(), "");

        try {
            // token validation
            Claims claims = Jwts.parser()
                .setSigningKey(jwtConf.getSecret().getBytes())
                .parseClaimsJws(token)
                .getBody();

            String username = claims.getSubject();
            if (username != null) {
                @SuppressWarnings("unchecked")
                List<String> authorities = (List<String>) claims.get("authorities");

                // TODO don't need authorities
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    username, null,
                    authorities
                        .stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())
                );

                SecurityContextHolder.getContext().setAuthentication(auth);
            }

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }

        chain.doFilter(request, response);
    }
}