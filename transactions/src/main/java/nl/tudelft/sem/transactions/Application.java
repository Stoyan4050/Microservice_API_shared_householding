package nl.tudelft.sem.transactions;

import nl.tudelft.sem.transactions.config.JwtConf;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableEurekaClient
@EnableJpaRepositories("nl.tudelft.sem.transactions.repositories")
@EntityScan("nl.tudelft.sem.transactions.entities")

@SuppressWarnings("PMD")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public JwtConf jwtConfig() {
        return new JwtConf();
    }
}
