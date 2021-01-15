package nl.tudelft.sem.transactions.config;

import org.springframework.beans.factory.annotation.Value;

public class JwtConf {
    @Value("${security.jwt.uri:/auth/**}")
    private String uri;
    @Value("${security.jwt.header:Authorization}")
    private String header;
    @Value("${security.jwt.prefix:Bearer }")
    private String prefix;
    @Value("${security.jwt.expiration:#{3*24*60*60}}")
    private int expiration;
    @Value("${security.jwt.secret:OZ9y3ADENrII}")
    private String secret;

    public String getHeader() {
        return header;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSecret() {
        return secret;
    }
}
