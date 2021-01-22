package nl.tudelft.sem.common.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import nl.tudelft.sem.auth.entities.UserCredentials;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserCredentialsTest {

    private static UserCredentials userCredentials;
    private static final String username = "user";
    private static final String password = "pass";

    @BeforeEach
    void setUp() {
        userCredentials = new UserCredentials();
    }

    @Test
    void getUsername() {
        userCredentials.setUsername(username);
        assertEquals(userCredentials.getUsername(), username);
    }

    @Test
    void getPassword() {
        userCredentials.setPassword(password);
        assertEquals(userCredentials.getPassword(), password);
    }

    @Test
    void testConstructor() {
        String newUser = "new user";
        String newPass = "new pass";
        userCredentials = new UserCredentials(newUser, newPass);
        assertEquals(userCredentials.getUsername(), newUser);
        assertEquals(userCredentials.getPassword(), newPass);
    }

}