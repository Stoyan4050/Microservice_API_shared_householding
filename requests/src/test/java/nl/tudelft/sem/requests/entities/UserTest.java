package nl.tudelft.sem.requests.entities;

import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

/**
 * Tests for the User entity class.
 */
public class UserTest {

    @Mock
    private static House mockHouse;

    private static User userUnderTest;


    @BeforeEach
    public void setUp() {
        initMocks(this);
    }

    @BeforeEach
    public void setUpAll() {
        userUnderTest = new User("userName", mockHouse, 0.0f, "email", Set.of(new Request()));
    }


    @Test
    public void constructorEqualsTest() {
        Assertions.assertEquals(userUnderTest,
                new User("userName", mockHouse, 0.0f, "email", Set.of(new Request())));
    }

    @Test
    public void constructorNotEqualsTest() {
        Assertions.assertNotEquals(userUnderTest,
                new User("userName2", mockHouse, 0.1f, "emaill", Set.of(new Request())));
    }

    @Test
    public void constructorNotEqualsObjectTest() {
        Assertions.assertFalse(userUnderTest.equals(new House(1, "sleepy")));
    }

    @Test
    public void testGetUsername() {
        final boolean result = userUnderTest.getUsername().equals("userName");
        System.out.println(userUnderTest.getUsername());
        Assertions.assertTrue(result);
    }

    @Test
    public void testSetUsername() {
        userUnderTest.setUsername("username2");
        final boolean result = userUnderTest.getUsername().equals("username2");
        Assertions.assertTrue(result);
    }

    @Test
    public void testSetAndGetHouse() {
        House emptyHouse = new House();
        userUnderTest.setHouse(emptyHouse);
        final boolean result = userUnderTest.getHouse().equals(emptyHouse);
        Assertions.assertTrue(result);
    }

    @Test
    public void testGetTotalCredits() {
        final boolean result = userUnderTest.getTotalCredits() == 0.0f;
        Assertions.assertTrue(result);
    }

    @Test
    public void testSetTotalCredits() {
        userUnderTest.setTotalCredits(0.1f);
        final boolean result = userUnderTest.getTotalCredits() == 0.1f;
        Assertions.assertTrue(result);
    }

    @Test
    public void testGetEmail() {
        final boolean result = userUnderTest.getEmail().equals("email");
        Assertions.assertTrue(result);
    }

    @Test
    public void testSetEmail() {
        userUnderTest.setEmail("newEmail");
        final boolean result = userUnderTest.getEmail().equals("newEmail");
        Assertions.assertTrue(result);
    }

    @Test
    public void testGetAndSetRequests() {
        Set<Request> requests = Set.of(new Request());
        userUnderTest.setRequests(requests);
        final boolean result = userUnderTest.getRequests().equals(requests);
        Assertions.assertTrue(result);
    }

}