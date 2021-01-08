package nl.tudelft.sem.requests.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
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
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
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
        final ResponseEntity<String> result = userController.updateUser(
            userWithNewInfo, userWithNewInfo.getUsername());
        verify(userRepository, times(1)).save(userWithNewInfo);

        final ResponseEntity<String> expected = new ResponseEntity<>("User updated successfully!",
            HttpStatus.OK);

        assertEquals(expected, result);
    }

    @Test
    public void testUpdateUserServerError() {
        // set up the user with new info
        final User userWithNewInfo = new User("username", new House(1, "name"),
            10.0f, "email", Set.of(new Request()));

        // set up the current user
        final User user = new User("username", new House(1, "name"),
            5.0f, "email", Set.of(new Request()));
        when(userRepository.findById("username")).thenReturn(Optional.of(user));
        when(userRepository.save(userWithNewInfo))
                .thenThrow(new MockitoException("User couldn't be updated!"));
        //doThrow(Exception.class).when(houseRepository).save(Mockito.any(House.class));

        // run the test and verify the results
        final ResponseEntity<String> result =
                userController.updateUser(userWithNewInfo, "username");

        final ResponseEntity<String> expected = new ResponseEntity<>("User couldn't be updated!",
            HttpStatus.INTERNAL_SERVER_ERROR);

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
        final ResponseEntity<String> result = userController.updateUser(
            userWithNewInfo, userWithNewInfo.getUsername());

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

        //userRepository.save(user);
        when(userRepository.findById("username")).thenReturn(Optional.of(user));

        // run the test
        final ResponseEntity<String> result = userController.getCreditsStatusForGroceries(
            "username");

        final ResponseEntity<String> expected = new ResponseEntity<>(HttpStatus.OK);

        // verify the results
        assertEquals(expected, result);
    }

    @Test
    public void testGetCreditsStatusForGroceriesForbidden() {
        // set up the user
        final User user = new User("a", new House(1, "name"),
            -60.0f, "email", Set.of(new Request()));

        //userRepository.save(user);
        when(userRepository.findById("a")).thenReturn(Optional.of(user));

        // run the test
        final ResponseEntity<String> result = userController.getCreditsStatusForGroceries("a");

        final ResponseEntity<String> expected = new ResponseEntity<>("Your credits are "
            + "less than -50! You should buy groceries.", HttpStatus.FORBIDDEN);

        // verify the results
        assertEquals(expected, result);
    }

    @Test
    public void testGetCreditsStatusForGroceriesNotFound() {
        // set up the user
        final User user = new User("a", new House(1, "name"),
            -60.0f, "email", Set.of(new Request()));

        userRepository.save(user);
        //when(userRepository.findById("username")).thenReturn(Optional.of(user));

        // run the test
        final ResponseEntity<String> result = userController.getCreditsStatusForGroceries("a");

        final ResponseEntity<String> expected = new ResponseEntity<>(HttpStatus.NOT_FOUND);

        // verify the results
        assertEquals(expected, result);
    }

    @Test
    public void testEditUserCreditsOkAdd() {
        // set up the user
        final User user = new User("hungry", new House(1, "CoolHouse"),
            10.0f, "email", Set.of(new Request()));

        when(userRepository.findByUsername("hungry")).thenReturn(user);
        when(userRepository.updateUserCredits(1, "email", 40, "hungry")).thenReturn(1);

        // run the test
        final ResponseEntity<?> result = userController.editUserCredits("hungry", 30, true);

        final ResponseEntity<?> expected = new ResponseEntity<>(HttpStatus.CREATED);

        // verify the results
        assertEquals(expected.getStatusCode(), result.getStatusCode());
    }

    @Test
    public void testEditUserCreditsOkSubstract() {
        // set up the user
        final User user = new User("hungry", new House(1, "CoolHouse"),
            10.0f, "email", Set.of(new Request()));

        when(userRepository.findByUsername("hungry")).thenReturn(user);
        when(userRepository.updateUserCredits(1, "email", -20, "hungry")).thenReturn(1);

        // run the test
        final ResponseEntity<?> result = userController.editUserCredits("hungry", 30, false);

        final ResponseEntity<?> expected = new ResponseEntity<>(HttpStatus.CREATED);

        // verify the results
        assertEquals(expected.getStatusCode(), result.getStatusCode());
    }

    @Test
    public void testEditUserCreditsBadRequestWrongCredits() {
        // set up the user
        final User user = new User("hungry", new House(1, "CoolHouse"),
            10.0f, "email", Set.of(new Request()));

        when(userRepository.findByUsername("hungry")).thenReturn(user);
        when(userRepository.updateUserCredits(1, "email", 40, "hungry")).thenReturn(0);

        // run the test
        final ResponseEntity<?> result = userController.editUserCredits("hungry", 30, true);

        final ResponseEntity<?> expected = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        // verify the results
        assertEquals(expected.getStatusCode(), result.getStatusCode());
    }

    @Test
    public void testEditUserCreditsBadRequest() {
        // set up the user
        final User user = new User("hungry", new House(1, "CoolHouse"),
            10.0f, "email", Set.of(new Request()));

        //when(userRepository.findByUsername("hungry")).thenReturn(user);
        when(userRepository.updateUserCredits(1, "email", 40, "hungry")).thenReturn(0);

        // run the test
        final ResponseEntity<?> result = userController.editUserCredits("hungry", 30, true);

        final ResponseEntity<?> expected = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        // verify the results
        assertEquals(expected.getStatusCode(), result.getStatusCode());
    }

    @Test
    public void testSplitUserCreditsTrue() {
        // set up the users
        final User user1 = new User("Sleepy", new House(1, "name"),
            5.0f, "email1", Set.of(new Request()));
        final User user2 = new User("Malwina", new House(1, "name"),
            15.0f, "email2", Set.of(new Request()));
        final User user3 = new User("Mocha", new House(1, "name"),
            10.0f, "email3", Set.of(new Request()));

        when(userRepository.findByUsername("Sleepy")).thenReturn(user1);
        when(userRepository.findByUsername("Malwina")).thenReturn(user2);
        when(userRepository.findByUsername("Mocha")).thenReturn(user3);

        List<String> users = new ArrayList<>();
        users.add(user1.getUsername());
        users.add(user2.getUsername());
        users.add(user3.getUsername());

        when(userRepository.updateUserCredits(1, "email1", 0, "Sleepy")).thenReturn(1);
        when(userRepository.updateUserCredits(1, "email2", 10, "Malwina")).thenReturn(1);
        when(userRepository.updateUserCredits(1, "email3", 5, "Mocha")).thenReturn(1);

        // run the test
        final boolean result = userController.splitUserCredits(users, 5);

        final boolean expected = true;

        // verify the results
        assertEquals(expected, result);
    }

    @Test
    public void testSplitUserCreditsFalse() {
        // set up the users
        final User user1 = new User("Sleepy", new House(1, "name"),
            5.0f, "email1", Set.of(new Request()));
        final User user2 = new User("Malwina", new House(1, "name"),
            15.0f, "email2", Set.of(new Request()));
        final User user3 = new User("Mocha", new House(1, "name"),
            10.0f, "email3", Set.of(new Request()));

        when(userRepository.findByUsername("Sleepy")).thenReturn(user1);
        when(userRepository.findByUsername("Malwina")).thenReturn(user2);
        when(userRepository.findByUsername("Mocha")).thenReturn(user3);

        List<String> users = new ArrayList<>();
        users.add(user1.getUsername());
        users.add(user2.getUsername());
        users.add(user3.getUsername());

        //when(userRepository.updateUserCredits(2, "email1", 10, "Sleepy")).thenReturn(0);
        //when(userRepository.updateUserCredits(1, "email2", 3, "Malwina")).thenReturn(0);
        //when(userRepository.updateUserCredits(1, "email3", 5, "Mocha")).thenReturn(0);
        when(userRepository.updateUserCredits(Mockito.anyInt(),
                Mockito.anyString(), Mockito.anyFloat(),
                Mockito.anyString())).thenReturn(0);
        //Mockito.doThrow(Exception.class).when(userRepository).updateUserCredits(Mockito.anyInt(),
        //Mockito.anyString(), Mockito.anyFloat(), Mockito.anyString());

        // run the test
        final boolean result = userController.splitUserCredits(users, 1);

        final boolean expected = false;

        // verify the results
        assertEquals(!expected, result);
    }

}