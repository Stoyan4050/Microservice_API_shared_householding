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
import nl.tudelft.sem.requests.entities.User;
import nl.tudelft.sem.requests.repositories.HouseRepository;
import nl.tudelft.sem.requests.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;


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
}