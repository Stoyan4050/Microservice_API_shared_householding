package nl.tudelft.sem.template.entities;

import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HouseTest {

    private static House houseUnderTest;

    @BeforeEach
    public void setUpAll() {
        houseUnderTest = new House(1, "name", Set.of(new Request()), Set.of(new User()));
    }


    @Test
    public void constructorEqualsTest() {

        Assertions.assertEquals(houseUnderTest,
                new House(1, "name", Set.of(new Request()), Set.of(new User())));
    }

    @Test
    public void constructorNotEqualsTest() {

        Assertions.assertNotEquals(houseUnderTest,
                new House(1, "noName", Set.of(new Request()), Set.of(new User())));
    }

    @Test
    public void testGetHouseNr() {
        final boolean result = houseUnderTest.getHouseNr() == 1; //??
        Assertions.assertTrue(result);
    }

    @Test
    public void testSetHouseNr() {
        houseUnderTest.setHouseNr(11);
        final boolean result = houseUnderTest.getHouseNr() == 11; //??
        Assertions.assertTrue(result);
    }

    @Test
    public void testGetName() {
        final boolean result = houseUnderTest.getName().equals("name");
        Assertions.assertTrue(result);
    }

    @Test
    public void testSetName() {
        houseUnderTest.setName("newName");
        final boolean result = houseUnderTest.getName().equals("newName");
        Assertions.assertTrue(result);
    }

    @Test
    public void testSetAndGetRequests() {
        Set<Request> requests = Set.of(new Request());
        houseUnderTest.setRequests(requests);
        final boolean result = houseUnderTest.getRequests().equals(requests);
        Assertions.assertTrue(result);
    }


    @Test
    public void testGetAndSetUsers() {
        Set<User> users = Set.of(new User());
        houseUnderTest.setUsers(users);
        final boolean result = houseUnderTest.getUsers().equals(users);
        Assertions.assertTrue(result);
    }

}
