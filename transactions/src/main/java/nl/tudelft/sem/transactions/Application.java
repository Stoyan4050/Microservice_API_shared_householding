package nl.tudelft.sem.transactions;

import com.netflix.discovery.EurekaClient;
import nl.tudelft.sem.transactions.config.JwtConf;
import nl.tudelft.sem.transactions.handlers.HouseValidator;
import nl.tudelft.sem.transactions.handlers.ProductValidator;
import nl.tudelft.sem.transactions.handlers.TokensValidator;
import nl.tudelft.sem.transactions.handlers.TransactionValidator;
import nl.tudelft.sem.transactions.handlers.Validator;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private transient EurekaClient discoveryClient;

    /**
     * Register a new validator bean that initializes the product and token validators.
     *
     * @return The newly created validator.
     */
    @Bean
    public Validator validate() {

        Validator handler = new ProductValidator();

        handler.setNext(new HouseValidator())
                .setNext(new TransactionValidator(discoveryClient))
                .setNext(new TokensValidator());

        return handler;
    }
}
