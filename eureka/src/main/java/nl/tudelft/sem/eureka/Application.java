package nl.tudelft.sem.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@SpringBootApplication
@EnableEurekaServer
@SuppressWarnings("PMD")
public class Application {

    @SuppressWarnings("PMD")
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    // since spring security enables csrf protection by default now, it needs to be disabled
    // otherwise the eureka server is always responding with 401 unauthenticated
    // https://github.com/spring-cloud/spring-cloud-netflix/issues/2754#issuecomment-372808529
    @EnableWebSecurity
    static class WebSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable();
        }
    }
}