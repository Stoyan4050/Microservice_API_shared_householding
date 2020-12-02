package nl.tudelft.sem.requests;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("nl.tudelft.sem.requests.repositories")
@EntityScan("nl.tudelft.sem.template")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
