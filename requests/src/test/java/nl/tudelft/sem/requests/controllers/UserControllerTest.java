package nl.tudelft.sem.requests.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        // set up the users
        final List<User> users = Arrays.asList(new User("username"));
        when(userRepository.findAll()).thenReturn(users);

        // run the test
        final List<User> result = userController.getAllUsers();

        // verify the results
        assertEquals(users.size(), result.size());
        assertEquals(users.get(0), result.get(0));
    }


    @Test
    public void testGetUserByUsername() {
        // set up the user
        final Optional<User> user = Optional.of(new User("username"));
        when(userRepository.findById("username")).thenReturn(user);

        // run the test
        final Optional<User> result = userController.getUserByUsername("username");

        // verify the results
        assertEquals(user, result);
    }

    @Test
    public void testAddUser() {
        // set up the user
        final User newUser = new User("username");

        // run the test
        userController.addNewUser(newUser);

        // verify the results
        verify(userRepository, times(1)).save(newUser);
    }

    @Test
    public void testUpdateUser() {
        // set up the user with new info
        final User userWithNewInfo = new User("username", new House(1, "name"),
                10.0f, "email", Set.of(new Request()));

        // set up the current user
        final User user = new User("username", new House(1, "name"),
                5.0f, "email", Set.of(new Request()));
        when(userRepository.findById("username")).thenReturn(Optional.of(user));

        // run the test and verify the results
        final ResponseEntity<String> result = userController.updateUser(userWithNewInfo, userWithNewInfo.getUsername());
        verify(userRepository, times(1)).save(user);

        final ResponseEntity<String> expected = new ResponseEntity("User updated successfully!",
                    HttpStatus.OK);

        assertEquals(expected, result);
    }

    @Test
    public void testUpdateUserNotFound() {
        // set up the user with new info
        final User userWithNewInfo = new User("username", new House(1, "name"),
            10.0f, "email", Set.of(new Request()));

        // set up the current user
        final User user = new User("username", new House(1, "name"),
            5.0f, "email", Set.of(new Request()));
        //when(userRepository.findById("username")).thenReturn(Optional.of(user));

        // run the test
        final ResponseEntity<String> result = userController.updateUser(userWithNewInfo, userWithNewInfo.getUsername());

        final ResponseEntity<String> expected = new ResponseEntity("User not found!",
                    HttpStatus.NOT_FOUND);

        // verify the results
        assertEquals(expected, result);
    }

    @Test
    public void testDeleteUser() {
        // set up the user
        Optional<User> user = Optional.of(new User("username2"));
        when(userRepository.findById("username2")).thenReturn(user);

        // run the test
        userController.deleteUser("username2");

        // verify the results
        verify(userRepository, times(1)).deleteById("username2");
    }

    @Test
    public void testDeleteUser2() {
        // in this situation there's no user with username "username1",
        // thus the deleteById won't be invoked

        // set up the user
        Optional<User> user = Optional.ofNullable(null);
        when(userRepository.findById("username1")).thenReturn(user);

        // run the test
        userController.deleteUser("username1");

        // verify the results
        verify(userRepository, times(0)).deleteById("username2");
    }

    @Test
    public void testGetCreditsStatusForGroceriesOk() {
        // set up the user
        final User user = new User("username", new House(1, "name"),
                5.0f, "email", Set.of(new Request()));

        userRepository.save(user);

        // run the test
        final ResponseEntity<String> result = userController.getCreditsStatusForGroceries("username");

        // verify the results
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), result);
    }

    @Test
    public void testGetCreditsStatusForGroceriesForbidden() {
        // set up the user
        final User user = new User("a", new House(1, "name"),
                -60.0f, "email", Set.of(new Request()));

        userRepository.save(user);

        // run the test
        final ResponseEntity<String> result = userController.getCreditsStatusForGroceries("a");

        // verify the results
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), result);
    }
}