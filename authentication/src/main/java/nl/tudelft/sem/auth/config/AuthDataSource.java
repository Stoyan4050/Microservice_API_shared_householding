package nl.tudelft.sem.auth.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EnableJpaRepositories
@PropertySource("application.properties")
@EnableTransactionManagement
public class AuthDataSource {
    @Autowired
    private Environment environment;

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /**
     * Create a datasource that communicates with the authentication database.
     *
     * @return a datasource that can add/authenticate users.
     */
    @Bean
    public DataSource dataSource() {
        final DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(environment.getProperty("auth.driver"));
        dataSourceBuilder.url(environment.getProperty("auth.url"));
        dataSourceBuilder.username(environment.getProperty("auth.user"));
        dataSourceBuilder.password(environment.getProperty("auth.password"));

        return dataSourceBuilder.build();
    }
}
