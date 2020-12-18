package nl.tudelft.sem.transactions.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class UsernameResolver implements HandlerMethodArgumentResolver {
    @Autowired
    private transient JwtConf jwtConf;

    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(Username.class) != null;
    }

    /**
     * Resolve the arguments on controllers that use the @Username annotation.
     *
     * @param webRequest The HTTP request containing the headers and the JWT token.
     * @return Username extracted from a JWT token in the request.
     */
    public String resolveArgument(MethodParameter methodParameter,
                                  ModelAndViewContainer mvContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        String header = webRequest.getHeader(jwtConf.getHeader());
        String token = header.replace(jwtConf.getPrefix(), "");
        Claims claims = Jwts.parser()
            .setSigningKey(jwtConf.getSecret().getBytes())
            .parseClaimsJws(token)
            .getBody();

        String username = claims.getSubject();
        return username;
    }

}
