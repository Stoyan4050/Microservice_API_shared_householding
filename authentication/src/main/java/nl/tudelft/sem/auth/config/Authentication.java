package nl.tudelft.sem.auth.config;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

@Configuration
@EnableWebSecurity
public class Authentication extends WebSecurityConfigurerAdapter {
    @Autowired
    private transient DataSource dataSource;
    @Autowired
    private transient JwtConf jwtConf;

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().dataSource(dataSource)
            .usersByUsernameQuery(
                "select username, password, enabled from users where binary username = ?");
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(
                (req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED)
            )
            .and()
            .addFilter(new JwtFilter(authenticationManager(), jwtConf))
            .authorizeRequests()
            .antMatchers(HttpMethod.POST, jwtConf.getUri()).permitAll()
            .anyRequest().authenticated();
    }

    /**
     * Create a new Bean for JDBC user detail management.
     * It is used for creating users and checking if users exist.
     *
     * @return A new JdbcUserDetailsManager
     */
    @Bean
    public JdbcUserDetailsManager jdbcUserDetailsManager() {
        final JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager();
        jdbcUserDetailsManager.setDataSource(dataSource);

        return jdbcUserDetailsManager;
    }

    @Bean
    public JwtConf jwtConfig() {
        return new JwtConf();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
