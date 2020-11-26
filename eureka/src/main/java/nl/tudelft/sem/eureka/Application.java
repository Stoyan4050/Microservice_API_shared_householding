package nl.tudelft.sem.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
@SuppressWarnings("PMD")
public class Application {

    @SuppressWarnings("PMD")
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}