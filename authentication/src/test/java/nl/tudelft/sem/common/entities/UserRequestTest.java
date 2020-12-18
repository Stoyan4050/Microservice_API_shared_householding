package nl.tudelft.sem.common.entities;

import nl.tudelft.sem.auth.entities.UserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserRequestTest {

    private static UserRequest userRequest;
    private static final String username = "user";
    private static final String email = "pass";
    @BeforeEach
    void setup() {
        userRequest = new UserRequest();
    }

    @Test
    void getUsername() {
        userRequest.setUsername(username);
        assertEquals(userRequest.getUsername(), username);
    }

    @Test
    void getEmail() {
        userRequest.setEmail(email);
        assertEquals(userRequest.getEmail(), email);
    }

    @Test
    void testConstructor() {
        String newUser = "new user";
        String newEmail = "new email";
        userRequest = new UserRequest(newUser, newEmail);
        assertEquals(userRequest.getUsername(), newUser);
        assertEquals(userRequest.getEmail(), newEmail);
    }

}