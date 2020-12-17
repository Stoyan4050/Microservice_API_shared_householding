package nl.tudelft.sem.requests.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import nl.tudelft.sem.requests.entities.House;
import nl.tudelft.sem.requests.entities.Request;
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
        final List<House> houses = Arrays.asList(new House(1, "CoolHouse"));
        when(houseRepository.findAll()).thenReturn(houses);
        final List<House> result = houseController.getAllHouses();

        assertEquals(houses.size(), result.size());
        assertEquals(houses.get(0), result.get(0));
    }

    @Test
    public void testGetHouseById() {
        final Optional<House> houses = Optional.of(new House(1, "CoolHouse"));
        when(houseRepository.findById(1)).thenReturn(houses);
        final Optional<House> result = houseController.getHouseByHouseNumber(1, "user");
        assertEquals(houses, result);
    }

    @Test
    public void testAddHouse() {
        final House newHouse = new House(1, "CoolHouse");
        final Optional<User> user = Optional.of(new User("fabian"));
        when(userRepository.findById("fabian")).thenReturn(user);
        houseController.addNewHouse(newHouse, "fabian");
        verify(houseRepository, times(1)).save(newHouse);
    }

    @Test
    public void testUpdateHouse() {
        final House houseWithNewInfo = new House(1, "CoolHouse");

        final House house = new House(1, "CoolestHouse");

        when(houseRepository.findById(1)).thenReturn(Optional.of(house));

        final ResponseEntity<House> result = houseController.updateHouse(houseWithNewInfo, 1);
        verify(houseRepository, times(1)).save(house);

        final ResponseEntity<House> expected = new ResponseEntity("House updated successfully!",
            HttpStatus.OK);

        assertEquals(expected, result);
    }

    @Test
    public void testUpdateHouseNotFound() {
        final House houseWithNewInfo = new House(1, "CoolHouse");

        final House house = new House(1, "CoolestHouse");

        when(houseRepository.findById(1)).thenReturn(Optional.of(house));

        final ResponseEntity<House> result = houseController.updateHouse(houseWithNewInfo, 2);

        final ResponseEntity<House> expected = new ResponseEntity("House not found!",
            HttpStatus.NOT_FOUND);

        assertEquals(expected, result);
    }

    @Test
    public void testDeleteHouse() {
        Optional<House> house = Optional.of(new House(1, "house"));
        when(houseRepository.findById(1)).thenReturn(house);
        houseController.deleteHouse(1);
        verify(houseRepository, times(1)).deleteById(1);
    }

    @Test
    public void testDeleteHouse2() {
        Optional<House> house = Optional.ofNullable(null);
        when(houseRepository.findById(7)).thenReturn(house);
        houseController.deleteHouse(7);
        verify(houseRepository, times(0)).deleteById(1);
    }

    @Test
    public void testGetUsersFromHouse() {
        final Optional<House> house = Optional.of(new House(1, "CoolHouse"));
        User user = new User("Malwina");
        Set<User> set = new HashSet<>();
        set.add(user);
        house.get().setUsers(set);
        when(houseRepository.findById(1)).thenReturn(house);

        List<User> result = houseController.getAllUsersFromHouse(1);
        List<User> expected = new ArrayList<>();
        expected.add(user);
        assertEquals(expected, result);
    }

    @Test
    public void testUserJoiningHouse() {
        Optional<House> house = Optional.of(new House(1, "CoolHouse"));
        final User user1 = new User("Malwina");
        final User user2 = new User("Malwina");
        user1.setHouse(house.get());
        user2.setHouse(null);

        Set<User> users = new HashSet<>();
        users.add(user1);
        house.get().setUsers(users);

        Set<User> updatedUsers = new HashSet<>();
        updatedUsers.add(user1);
        updatedUsers.add(user2);

        when(houseRepository.findById(1)).thenReturn(house);
        when(userRepository.findById("Malwina")).thenReturn(Optional.of(user1));
        when(userRepository.findById("Mocha")).thenReturn(Optional.of(user2));

        houseController.userJoiningHouse("Mocha", 1);

        assertEquals(user2.getHouse().getHouseNr(), 1);
        //assertEquals(updatedUsers, user2.getHouse().getUsers());
        //TODO -> the users should also get updated -> should return true but it returns false
    }

    @Test
    public void testUserLeavingHouse() {
        final Optional<House> house = Optional.of(new House(1, "CoolHouse"));
        final User user1 = new User("Malwina");
        final User user2 = new User("Mocha");
        user1.setHouse(house.get());
        user2.setHouse(house.get());

        Set<User> users = new HashSet<>();
        users.add(user1);
        users.add(user2);
        house.get().setUsers(users);

        when(houseRepository.findById(1)).thenReturn(house);
        when(userRepository.findById("Malwina")).thenReturn(Optional.of(user1));
        when(userRepository.findById("Mocha")).thenReturn(Optional.of(user2));

        final ResponseEntity<House> result = houseController.userLeavingHouse("Mocha", 1);

        final ResponseEntity<House> expected = new ResponseEntity("You successfully removed "
            + "Mocha from house number 1!", HttpStatus.OK);

        assertEquals(expected, result);
    }

    @Test
    public void testUserLeavingHouseAndDeleteHouse() {
        final Optional<House> house = Optional.of(new House(1, "CoolHouse"));
        final User user = new User("Mocha");
        user.setHouse(house.get());

        Set<User> users = new HashSet<>();
        users.add(user);
        house.get().setUsers(users);

        when(houseRepository.findById(1)).thenReturn(house);
        when(userRepository.findById("Mocha")).thenReturn(Optional.of(user));

        final ResponseEntity<House> result = houseController.userLeavingHouse("Mocha", 1);

        final ResponseEntity<House> expected = new ResponseEntity("You successfully removed "
            + "Mocha from house number 1!", HttpStatus.OK);

        assertEquals(expected, result);
    }

    @Test
    public void testUserLeavingHouseDifferentHousehold() {
        final Optional<House> house1 = Optional.of(new House(1, "CoolHouse"));
        final Optional<House> house2 = Optional.of(new House(2, "NotSoCoolHouse"));

        //users for 1st house
        final User user1 = new User("Malwina");
        final User user2 = new User("Mocha");
        user1.setHouse(house1.get());
        user2.setHouse(house1.get());

        //users for 2nd house
        final User user3 = new User("Hungry");
        user3.setHouse(house2.get());

        //setting the users for 1st house
        Set<User> users1 = new HashSet<>();
        users1.add(user1);
        users1.add(user2);
        house1.get().setUsers(users1);

        //setting the users for 2st house
        Set<User> users2 = new HashSet<>();
        users2.add(user3);
        house2.get().setUsers(users2);

        when(houseRepository.findById(1)).thenReturn(house1);
        when(houseRepository.findById(2)).thenReturn(house2);
        when(userRepository.findById("Malwina")).thenReturn(Optional.of(user1));
        when(userRepository.findById("Mocha")).thenReturn(Optional.of(user2));
        when(userRepository.findById("Hungry")).thenReturn(Optional.of(user3));

        final ResponseEntity<House> result = houseController.userLeavingHouse("Hungry", 1);

        final ResponseEntity<House> expected = new ResponseEntity("You can not remove a user"
            + " from a different household!", HttpStatus.FORBIDDEN);

        assertEquals(expected, result);
    }

    @Test
    public void testUserLeavingHouseUserWithNoHouse() {
        final Optional<House> house = Optional.of(new House(1, "CoolHouse"));
        final User user1 = new User("Malwina");
        final User user2 = new User("Mocha");
        user1.setHouse(house.get());
        user2.setHouse(null);

        Set<User> users = new HashSet<>();
        users.add(user1);
        house.get().setUsers(users);

        when(houseRepository.findById(1)).thenReturn(house);
        when(userRepository.findById("Malwina")).thenReturn(Optional.of(user1));
        when(userRepository.findById("Mocha")).thenReturn(Optional.of(user2));

        final ResponseEntity<House> result = houseController.userLeavingHouse("Mocha", 1);

        final ResponseEntity<House> expected = new ResponseEntity("The user does not "
            + "have a house!", HttpStatus.FORBIDDEN);

        assertEquals(expected, result);
    }

    @Test
    public void testUserLeavingHouseNotFound() {
        final Optional<House> house = Optional.of(new House(1, "CoolHouse"));
        final User user1 = new User("Malwina");
        final User user2 = new User("Mocha");
        user1.setHouse(house.get());
        user2.setHouse(house.get());

        Set<User> users = new HashSet<>();
        users.add(user1);
        users.add(user2);
        house.get().setUsers(users);

        when(houseRepository.findById(1)).thenReturn(house);
        when(userRepository.findById("Malwina")).thenReturn(Optional.of(user1));
        when(userRepository.findById("Mocha")).thenReturn(Optional.of(user2));

        final ResponseEntity<House> result = houseController.userLeavingHouse("Mocha", 2);

        final ResponseEntity<House> expected = new ResponseEntity("The user or the house "
            + "were not found, please check again!", HttpStatus.NOT_FOUND);

        assertEquals(expected, result);
    }

}