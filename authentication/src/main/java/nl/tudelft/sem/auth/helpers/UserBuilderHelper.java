package nl.tudelft.sem.auth.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserBuilderHelper {
    @Autowired
    private transient PasswordEncoder passwordEncoder;

    /**
     * Helper method that uses this.passwordEncoder to encode a given password.
     *
     * @param password Password to be encoded.
     * @return The encoded password as a String.
     */
    private String encodePassword(String password) {
        return this.passwordEncoder.encode(password);
    }

    /**
     * Build a UserDetails object that can be used by jdbcUserDetailsManager
     * to add new users to the database.
     *
     * @param username Username of the user that will be used to build a UserDetails object.
     * @param password Password of the user that will be used to build a UserDetails object.
     * @return The built UserDetails object.
     */
    org.springframework.security.core.userdetails.UserDetails buildUser(String username,
                                                                               String password) {
        return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(encodePassword(password))
                .roles("USER")
                .build();
    }
}
