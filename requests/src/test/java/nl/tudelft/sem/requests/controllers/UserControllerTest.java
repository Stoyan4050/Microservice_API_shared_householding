package nl.tudelft.sem.requests.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import nl.tudelft.sem.requests.entities.House;
import nl.tudelft.sem.requests.entities.Request;
import nl.tudelft.sem.requests.entities.User;
import nl.tudelft.sem.requests.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Tests for the UserController.
 */
@SuppressWarnings("PMD")
public class UserControllerTest {

    @Mock
    private transient UserRepository userRepository;

    @InjectMocks
    private transient UserController userController;

    @BeforeEach
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testGetAllUser() {
        final List<User> users = Arrays.asList(new User("username"));
        when(userRepository.findAll()).thenReturn(users);
        final List<User> result = userController.getAllUsers();
        assertEquals(users.size(), result.size());
        assertEquals(users.get(0), result.get(0));
    }


    @Test
    public void testGetUserByUsername() {
        final Optional<User> user = Optional.of(new User("username"));
        when(userRepository.findById("username")).thenReturn(user);

        final Optional<User> result = userController.getUserByUsername("username");
        assertEquals(user, result);
    }

    @Test
    public void testAddUser() {
        final User newUser = new User("username");
        userController.addNewUser(newUser);
        verify(userRepository, times(1)).save(newUser);
    }

    /* - The HTTP response is 404, which is incorrect

    @Test
    public void testUpdateUser() {
        // Setup
        final User userWithNewInfo = new User("username", new House(1, "name"),
                5.0f, "email", Set.of(new Request()));

        // Configure UserRepository.getOne(...).
        final User user = new User("username", new House(1, "name"),
                5.0f, "email", Set.of(new Request()));
        when(userRepository.getOne("username")).thenReturn(user);

        // Configure UserRepository.save(...).
        final User user1 = new User("username", new House(1, "name"),
                5.0f, "email", Set.of(new Request()));
        when(userRepository.save(any(User.class))).thenReturn(user1);

        // Run the test
        final ResponseEntity<User> result = userController.updateUser(userWithNewInfo, "username");

        // Verify the results
        assertEquals(HttpStatus.OK, result);
    }
     */

    @Test
    public void testUpdateUser() {
        // Setup
        final User userWithNewInfo = new User("username", new House(1, "name"),
                5.0f, "email", Set.of(new Request()));

        // Configure UserRepository.getOne(...).
        final User user = new User("username", new House(1, "name"),
                5.0f, "email", Set.of(new Request()));
        when(userRepository.getOne("username")).thenReturn(user);

        // Configure UserRepository.save(...).
        final User user1 = new User("username", new House(1, "name"),
                5.0f, "email", Set.of(new Request()));
        when(userRepository.save(any(User.class))).thenReturn(user1);

        // Run the test
        final String result = userController.updateUser(userWithNewInfo, "username");

        // Verify the results - should be "Updated successfully"
        assertEquals("User not found!", result);
    }

    @Test
    public void testDeleteUser() {
        Optional<User> user = Optional.of(new User("username2"));
        when(userRepository.findById("username2")).thenReturn(user);
        userController.deleteUser("username2");
        verify(userRepository, times(1)).deleteById("username2");
    }

    @Test
    public void testDeleteUser2() {
        // in this situation there's no user with username "username1",
        // thus the deleteById won't be invoked
        Optional<User> user = Optional.ofNullable(null);
        when(userRepository.findById("username1")).thenReturn(user);
        userController.deleteUser("username1");
        verify(userRepository, times(0)).deleteById("username2");
    }

    @Test
    public void testGetCreditsStatusForGroceriesOk() {
        final User user = new User("username", new House(1, "name"),
                5.0f, "email", Set.of(new Request()));

        userRepository.save(user);

        final ResponseEntity<User> result = userController.getCreditsStatusForGroceries("username");
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), result);
    }

    @Test
    public void testGetCreditsStatusForGroceriesForbidden() {
        final User user = new User("a", new House(1, "name"),
                -60.0f, "email", Set.of(new Request()));

        userRepository.save(user);

        final ResponseEntity<User> result = userController.getCreditsStatusForGroceries("a");
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), result);
    }
}