package nl.tudelft.sem.common.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import nl.tudelft.sem.auth.entities.UserRegister;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserRegisterTest {

    private static UserRegister userRegister;
    private static final String username = "user";
    private static final String password = "pass";
    private static final String email = "asdf@asdf.com";

    @BeforeEach
    void setUp() {
        userRegister = new UserRegister();
    }

    @Test
    void getUsername() {
        userRegister.setUsername(username);
        assertEquals(userRegister.getUsername(), username);
    }

    @Test
    void getPassword() {
        userRegister.setPassword(password);
        assertEquals(userRegister.getPassword(), password);
    }

    @Test
    void getEmail() {
        userRegister.setEmail(email);
        assertEquals(userRegister.getEmail(), email);
    }

    @Test
    void testConstructor() {
        String newUser = "new user";
        String newPass = "new pass";
        String newEmail = "new email";
        userRegister = new UserRegister(newUser, newPass, newEmail);
        assertEquals(userRegister.getUsername(), newUser);
        assertEquals(userRegister.getPassword(), newPass);
        assertEquals(userRegister.getEmail(), newEmail);
    }

}