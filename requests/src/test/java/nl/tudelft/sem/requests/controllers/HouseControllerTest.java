package nl.tudelft.sem.requests.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import nl.tudelft.sem.requests.entities.House;
import nl.tudelft.sem.requests.entities.User;
import nl.tudelft.sem.requests.repositories.HouseRepository;
import nl.tudelft.sem.requests.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


/**
 * Tests for the HouseController.
 */
@SuppressWarnings("PMD")
public class HouseControllerTest {

    @Mock
    private transient HouseRepository houseRepository;

    @Mock
    private transient UserRepository userRepository;

    @InjectMocks
    private transient HouseController houseController;

    @BeforeEach
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testGetAllHouses() {
        // set up the houses
        final List<House> houses = Arrays.asList(new House(1, "CoolHouse"));
        when(houseRepository.findAll()).thenReturn(houses);

        // run the test
        final List<House> result = houseController.getAllHouses();

        // verify the results
        assertEquals(houses.size(), result.size());
        assertEquals(houses.get(0), result.get(0));
    }

    @Test
    public void testGetHouseById() {
        // set up the house
        final Optional<House> houses = Optional.of(new House(1, "CoolHouse"));
        when(houseRepository.findById(1)).thenReturn(houses);

        // run the test
        final ResponseEntity<House> result = houseController.getHouseByHouseNumber(1);

        // verify the results
        assertEquals(result.getStatusCode(), HttpStatus.OK);
        assertEquals(houses.get(), result.getBody());
    }

    @Test
    public void testAddHouse() {
        // set up the house
        final House newHouse = new House(1, "CoolHouse");
        final Optional<User> user = Optional.of(new User("fabian"));
        when(userRepository.findById("fabian")).thenReturn(user);

        // run the test
        houseController.addNewHouse(newHouse, "fabian");

        // verify the results
        verify(houseRepository, times(1)).save(newHouse);
    }

    @Test
    public void testUpdateHouse() {
        // set up the house with new info
        final House houseWithNewInfo = new House(1, "CoolHouse");

        // set up the current house
        final House house = new House(1, "CoolestHouse");
        when(houseRepository.findById(1)).thenReturn(Optional.of(house));

        // run the test and verify the results
        final ResponseEntity<String> result = houseController.updateHouse(houseWithNewInfo);
        verify(houseRepository, times(1)).save(houseWithNewInfo);

        final ResponseEntity<String> expected = new ResponseEntity<>("House updated successfully!",
            HttpStatus.OK);

        assertEquals(expected, result);
    }

    /*
    @Test
    public void testUpdateHouseServerError() {
        // set up the house with new info
        final House houseWithNewInfo = new House(1, "CoolHouse");

        // set up the current house
        final House house = new House(1, "CoolestHouse");
        when(houseRepository.findById(1)).thenReturn(Optional.of(house));


        // run the test and verify the results
        final ResponseEntity<House> result = houseController.updateHouse(houseWithNewInfo);
        verify(houseRepository, times(1)).save(houseWithNewInfo);

        final ResponseEntity<House> expected = new ResponseEntity("House couldn't be updated!",
            HttpStatus.INTERNAL_SERVER_ERROR);

        assertEquals(expected, result);
    }

     */

    @Test
    public void testUpdateHouseNotFound() {
        // set up the house with new info
        final House houseWithNewInfo = new House(1, "CoolHouse");

        // set up the current house
        final House house = new House(1, "CoolestHouse");
        //when(houseRepository.findById(1)).thenReturn(Optional.of(house));

        // run the test
        final ResponseEntity<String> result = houseController.updateHouse(houseWithNewInfo);

        final ResponseEntity<String> expected = new ResponseEntity<>("House not found!",
            HttpStatus.NOT_FOUND);

        // verify the results
        assertEquals(expected, result);
    }

    @Test
    public void testDeleteHouse() {
        // set up the house
        Optional<House> house = Optional.of(new House(1, "house"));
        when(houseRepository.findById(1)).thenReturn(house);

        // run the test
        houseController.deleteHouse(1);

        // verify the results
        verify(houseRepository, times(1)).deleteById(1);
    }

    @Test
    public void testDeleteHouse2() {
        // set up the house
        Optional<House> house = Optional.ofNullable(null);
        when(houseRepository.findById(7)).thenReturn(house);

        // run the test
        houseController.deleteHouse(7);

        // verify the results
        verify(houseRepository, times(0)).deleteById(1);
    }

    @Test
    public void testGetUsersFromHouse() {
        // set up the house
        final Optional<House> house = Optional.of(new House(1, "CoolHouse"));
        User user = new User("Malwina");

        // set up the members
        Set<User> set = new HashSet<>();
        set.add(user);
        house.get().setUsers(set);

        when(houseRepository.findById(1)).thenReturn(house);

        // run the test
        ResponseEntity<List<User>> result = houseController.getAllUsersFromHouse(1);

        List<User> expected = List.of(user);

        // verify the results
        assertEquals(result.getStatusCode(), HttpStatus.OK);
        assertEquals(expected, result.getBody());
    }

    @Test
    public void testUserJoiningHouse() {
        // set up the house
        Optional<House> house = Optional.of(new House(1, "CoolHouse"));
        final User user1 = new User("Malwina");
        final User user2 = new User("Mocha");
        user1.setHouse(house.get());
        user2.setHouse(null);

        // set up the members
        Set<User> users = new HashSet<>();
        users.add(user1);
        house.get().setUsers(users);

        // set up the updated members
        final User newUser = new User("Mocha");
        newUser.setHouse(house.get());
        Set<User> updatedUsers = new HashSet<>();
        updatedUsers.add(user1);
        updatedUsers.add(user2);

        when(houseRepository.findById(1)).thenReturn(house);
        when(userRepository.findById("Malwina")).thenReturn(Optional.of(user1));
        when(userRepository.findById("Mocha")).thenReturn(Optional.of(user2));

        // run the test and verify the results
        houseController.userJoiningHouse("Mocha", 1);
        house.get().getUsers().add(user2);
        verify(userRepository, times(1)).save(user2);

        assertEquals(user2.getHouse().getHouseNr(), 1);
        assertEquals(updatedUsers, user2.getHouse().getUsers());
    }

    @Test
    public void testUserLeavingHouse() {
        // set up the house
        final Optional<House> house = Optional.of(new House(1, "CoolHouse"));
        final User user1 = new User("Malwina");
        final User user2 = new User("Mocha");
        user1.setHouse(house.get());
        user2.setHouse(house.get());

        // set up the members
        Set<User> users = new HashSet<>();
        users.add(user1);
        users.add(user2);
        house.get().setUsers(users);

        when(houseRepository.findById(1)).thenReturn(house);
        when(userRepository.findById("Malwina")).thenReturn(Optional.of(user1));
        when(userRepository.findById("Mocha")).thenReturn(Optional.of(user2));

        // run the test
        final ResponseEntity<String> result = houseController.userLeavingHouse("Mocha", 1);

        final ResponseEntity<String> expected = new ResponseEntity<>("You successfully removed "
            + "Mocha from house number 1!", HttpStatus.OK);

        // verify the results
        assertEquals(expected, result);
    }

    @Test
    public void testUserLeavingHouseAndDeleteHouse() {
        // set up the house
        final Optional<House> house = Optional.of(new House(1, "CoolHouse"));
        final User user = new User("Mocha");
        user.setHouse(house.get());

        // set up the members
        Set<User> users = new HashSet<>();
        users.add(user);
        house.get().setUsers(users);

        when(houseRepository.findById(1)).thenReturn(house);
        when(userRepository.findById("Mocha")).thenReturn(Optional.of(user));

        // run the test
        final ResponseEntity<String> result = houseController.userLeavingHouse("Mocha", 1);

        final ResponseEntity<String> expected = new ResponseEntity<>("You successfully removed "
            + "Mocha from house number 1!", HttpStatus.OK);

        // verify the results
        assertEquals(expected, result);
    }

    @Test
    public void testUserLeavingHouseDifferentHousehold() {
        // set up the houses
        final Optional<House> house1 = Optional.of(new House(1, "CoolHouse"));
        final Optional<House> house2 = Optional.of(new House(2, "NotSoCoolHouse"));

        // users for 1st house
        final User user1 = new User("Malwina");
        final User user2 = new User("Mocha");
        user1.setHouse(house1.get());
        user2.setHouse(house1.get());

        // users for 2nd house
        final User user3 = new User("Hungry");
        user3.setHouse(house2.get());

        // setting the users for 1st house
        Set<User> users1 = new HashSet<>();
        users1.add(user1);
        users1.add(user2);
        house1.get().setUsers(users1);

        // setting the users for 2st house
        Set<User> users2 = new HashSet<>();
        users2.add(user3);
        house2.get().setUsers(users2);

        when(houseRepository.findById(1)).thenReturn(house1);
        when(houseRepository.findById(2)).thenReturn(house2);
        when(userRepository.findById("Malwina")).thenReturn(Optional.of(user1));
        when(userRepository.findById("Mocha")).thenReturn(Optional.of(user2));
        when(userRepository.findById("Hungry")).thenReturn(Optional.of(user3));

        // run the test
        final ResponseEntity<String> result = houseController.userLeavingHouse("Hungry", 1);

        final ResponseEntity<String> expected = new ResponseEntity<>("You can not remove a user"
            + " from a different household!", HttpStatus.FORBIDDEN);

        // verify the results
        assertEquals(expected, result);
    }

    @Test
    public void testUserLeavingHouseUserWithNoHouse() {
        // set up the house
        final Optional<House> house = Optional.of(new House(1, "CoolHouse"));
        final User user1 = new User("Malwina");
        final User user2 = new User("Mocha");
        user1.setHouse(house.get());
        user2.setHouse(null);

        // set up the members
        Set<User> users = new HashSet<>();
        users.add(user1);
        house.get().setUsers(users);

        when(houseRepository.findById(1)).thenReturn(house);
        when(userRepository.findById("Malwina")).thenReturn(Optional.of(user1));
        when(userRepository.findById("Mocha")).thenReturn(Optional.of(user2));

        // run the test
        final ResponseEntity<String> result = houseController.userLeavingHouse("Mocha", 1);

        final ResponseEntity<String> expected = new ResponseEntity<>("The user does not "
            + "have a house!", HttpStatus.FORBIDDEN);

        // verify the results
        assertEquals(expected, result);
    }

    @Test
    public void testUserLeavingHouseNotFound() {
        // set up the house
        final Optional<House> house = Optional.of(new House(1, "CoolHouse"));
        final User user1 = new User("Malwina");
        final User user2 = new User("Mocha");
        user1.setHouse(house.get());
        user2.setHouse(house.get());

        // set up the members
        Set<User> users = new HashSet<>();
        users.add(user1);
        users.add(user2);
        house.get().setUsers(users);

        when(houseRepository.findById(1)).thenReturn(house);
        when(userRepository.findById("Malwina")).thenReturn(Optional.of(user1));
        when(userRepository.findById("Mocha")).thenReturn(Optional.of(user2));

        // run the test
        final ResponseEntity<String> result = houseController.userLeavingHouse("Mocha", 2);

        final ResponseEntity<String> expected = new ResponseEntity<>("The user or the house "
            + "were not found, please check again!", HttpStatus.NOT_FOUND);

        // verify the results
        assertEquals(expected, result);
    }

}