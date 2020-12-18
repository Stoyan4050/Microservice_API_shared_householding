package nl.tudelft.sem.requests.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
import nl.tudelft.sem.requests.entities.Request;
import nl.tudelft.sem.requests.entities.RequestId;
import nl.tudelft.sem.requests.entities.User;
import nl.tudelft.sem.requests.repositories.HouseRepository;
import nl.tudelft.sem.requests.repositories.RequestRepository;
import nl.tudelft.sem.requests.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Tests for the RequestController class and RequestRepository.
 */
@SuppressWarnings("PMD")
public class RequestControllerTest {

    @Mock
    private transient RequestRepository requestRepository;

    @Mock
    private transient HouseRepository houseRepository;

    @Mock
    private transient UserRepository userRepository;

    @Mock
    private static RequestId requestIdMock;

    @InjectMocks
    private transient RequestController requestController;

    @BeforeEach
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testGetAllRequests() {
        // Setup

        // Configure RequestRepository.findAll(...).
        final List<Request> requests = Arrays.asList(new Request(requestIdMock,
                new House(1, "name"), new User("username"), true));
        when(requestRepository.findAll()).thenReturn(requests);

        // Run the test
        final List<Request> result = requestController.getAllRequests();

        // Verify the results
    }


    @Test
    public void testGetRequestById() {
        // Setup

        // Configure RequestRepository.findById(...).
        final Optional<Request> requests = Optional.of(new Request(requestIdMock,
                new House(1, "namee"), new User("usernamee"), true));
        when(requestRepository.findById(requestIdMock)).thenReturn(requests);

        // Run the test
        final Optional<Request> result = requestController.getRequestById(requestIdMock);

        // Verify the results
    }

    @Test
    public void testAddRequest() {
        // Setup
        final Request newRequest = new Request(requestIdMock,
                new House(1, "name"), new User("username"), true);

        // Configure RequestRepository.save(...).
        final Request request = new Request(requestIdMock,
                new House(1, "name"), new User("username"), true);
        when(requestRepository.save(any(Request.class))).thenReturn(request);

        // Run the test
        requestController.addRequest(newRequest);

        // Verify the results
    }

    @Test
    public void testUpdateRequest() {
        //set up the 1st house
        final Optional<House> house1 = Optional.of(new House(1, "CoolHouse"));
        final User user1 = new User("Malwina");
        user1.setHouse(house1.get());

        Set<User> users1 = new HashSet<>();
        users1.add(user1);
        house1.get().setUsers(users1);

        //set up the 2nd house
        final Optional<House> house2 = Optional.of(new House(2, "CoolestHouse"));
        final User user2 = new User("Mocha");
        user2.setHouse(house2.get());

        Set<User> users2 = new HashSet<>();
        users2.add(user2);
        house2.get().setUsers(users2);

        //set up the current request
        final Optional<User> newUser = Optional.of(new User("Ina"));

        final RequestId requestId1 = new RequestId(1, "Ina");
        final Request request = new Request(requestId1, house1.get(), newUser.get(),
            false);

        //set up the new request
        final RequestId requestId2 = new RequestId(2, "Ina");
        final Request requestWithNewInfo = new Request(requestId2, house2.get(), newUser.get(),
            false);

        when(houseRepository.findById(1)).thenReturn(house1);
        when(houseRepository.findById(2)).thenReturn(house2);
        when(userRepository.findById("Malwina")).thenReturn(Optional.of(user1));
        when(userRepository.findById("Mocha")).thenReturn(Optional.of(user2));
        when(userRepository.findById("Ina")).thenReturn(newUser);
        when(requestRepository.findById(requestId1)).thenReturn(Optional.of(request));
        when(requestRepository.findById(requestId2)).thenReturn(Optional.of(requestWithNewInfo));

        final ResponseEntity<Request> result = requestController.updateRequest(requestWithNewInfo,
            requestId1);
        verify(requestRepository, times(1)).save(requestWithNewInfo);

        final ResponseEntity<Request> expected = new ResponseEntity("Request updated successfully!",
            HttpStatus.OK);

        assertEquals(expected, result);
    }

    @Test
    public void testUpdateRequestNotFound() {
        //set up the 1st house
        final Optional<House> house1 = Optional.of(new House(1, "CoolHouse"));
        final User user1 = new User("Malwina");
        user1.setHouse(house1.get());

        Set<User> users1 = new HashSet<>();
        users1.add(user1);
        house1.get().setUsers(users1);

        //set up the 2nd house
        final Optional<House> house2 = Optional.of(new House(2, "CoolestHouse"));
        final User user2 = new User("Mocha");
        user2.setHouse(house2.get());

        Set<User> users2 = new HashSet<>();
        users2.add(user2);
        house2.get().setUsers(users2);

        //set up the current request
        final Optional<User> newUser = Optional.of(new User("Ina"));

        final RequestId requestId1 = new RequestId(1, "Ina");
        final Request request = new Request(requestId1, house1.get(), newUser.get(),
            false);

        //set up the new request
        final RequestId requestId2 = new RequestId(2, "Ina");
        final Request requestWithNewInfo = new Request(requestId2, house2.get(), newUser.get(),
            false);

        final RequestId requestId3 = new RequestId(2, "Inaa");

        when(houseRepository.findById(1)).thenReturn(house1);
        when(houseRepository.findById(2)).thenReturn(house2);
        when(userRepository.findById("Malwina")).thenReturn(Optional.of(user1));
        when(userRepository.findById("Mocha")).thenReturn(Optional.of(user2));
        when(userRepository.findById("Ina")).thenReturn(newUser);
        when(requestRepository.findById(requestId1)).thenReturn(Optional.of(request));
        when(requestRepository.findById(requestId2)).thenReturn(Optional.of(requestWithNewInfo));

        final ResponseEntity<Request> result = requestController.updateRequest(requestWithNewInfo,
            requestId3);

        final ResponseEntity<Request> expected = new ResponseEntity("Request not found!",
            HttpStatus.NOT_FOUND);

        assertEquals(expected, result);
    }

    @Test
    public void testRemoveRequest() {
        // Setup

        // Run the test
        requestController.removeRequest(requestIdMock);

        // Verify the results
        verify(requestRepository).deleteById(requestIdMock);
    }

    /*
    @Test
    public void testMembersAcceptingRequest() { //TODO -> returns request not found
        final Optional<House> house = Optional.of(new House(1, "CoolHouse"));
        final User user1 = new User("Malwina");
        final User user2 = new User("Mocha");
        final Optional<User> newUser = Optional.of(new User("Ina"));
        user1.setHouse(house.get());
        user2.setHouse(house.get());

        Set<User> users = new HashSet<>();
        users.add(user1);
        users.add(user2);
        house.get().setUsers(users);

        final RequestId requestId = new RequestId(1, "Ina");
        final Optional<Request> request = Optional.of(new Request(requestId, house.get(),
                                                        newUser.get(), false));

        when(houseRepository.findById(1)).thenReturn(house);
        when(userRepository.findById("Malwina")).thenReturn(Optional.of(user1));
        when(userRepository.findById("Mocha")).thenReturn(Optional.of(user2));
        when(userRepository.findById("Ina")).thenReturn(newUser);
        when(requestRepository.findById(requestId)).thenReturn(request);

        final ResponseEntity<Request> result = requestController.membersAcceptingRequest("Ina",
            1, "Mocha");


        final ResponseEntity<Request> expected = new ResponseEntity("You have successfully "
            + "accepted the user: " + request.get().getUser().getUsername(), HttpStatus.OK);

        assertEquals(expected, result);
    }
     */
}



