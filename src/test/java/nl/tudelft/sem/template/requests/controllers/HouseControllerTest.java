package nl.tudelft.sem.template.requests.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.requests.entities.House;
import nl.tudelft.sem.template.requests.repositories.HouseRepository;
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
        final Optional<House> result = houseController.getHouseByHouseNumber(1);
        assertEquals(houses, result);
    }

    @Test
    public void testAddHouse() {
        final House newHouse = new House(1, "CoolHouse");
        houseController.addNewHouse(newHouse);
        verify(houseRepository, times(1)).save(newHouse);
    }

    @Test
    public void testDeleteHouse() {
        houseController.deleteHouse(1);
        verify(houseRepository, times(1)).deleteById(1);
    }
}