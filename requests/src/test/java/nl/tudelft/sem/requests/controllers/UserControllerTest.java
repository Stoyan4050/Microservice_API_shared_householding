package nl.tudelft.sem.requests.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.requests.entities.User;
import nl.tudelft.sem.requests.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

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

    @Test
    public void testDeleteUser() {
        userController.deleteUser("username2");
        verify(userRepository, times(1)).deleteById("username2");
    }
}