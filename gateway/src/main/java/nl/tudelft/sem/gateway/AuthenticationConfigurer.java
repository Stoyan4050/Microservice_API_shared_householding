package nl.tudelft.sem.gateway;

import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class AuthenticationConfigurer extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtConf jwtConf;

    public JwtConf getJwtConf() {
        return jwtConf;
    }

    public void setJwtConf(JwtConf jwtConf) {
        this.jwtConf = jwtConf;
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {

        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().authenticationEntryPoint(
                        (req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                )
                .and()
                .addFilterAfter(
                        new JwtAuthFilter(jwtConf), UsernamePasswordAuthenticationFilter.class
                )
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, jwtConf.getUri()).permitAll()
                // any requests that are not POST
                // to jwtConf.uri ("/auth/**") should be authenticated
                .anyRequest().authenticated();
    }

    @Bean
    public JwtConf jwtConfig() {
        return new JwtConf();
    }

}
