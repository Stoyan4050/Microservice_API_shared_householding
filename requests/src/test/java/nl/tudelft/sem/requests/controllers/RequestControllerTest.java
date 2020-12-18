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
        // set up the requests
        final List<Request> requests = Arrays.asList(new Request(requestIdMock,
                new House(1, "name"), new User("username"), true));
        when(requestRepository.findAll()).thenReturn(requests);

        // run the test
        final List<Request> result = requestController.getAllRequests();

        // verify the results
        verify(requestRepository).findAll();
        assertEquals(requests, result);
    }


    @Test
    public void testGetRequestById() {
        // set up the request
        final Request request = new Request(requestIdMock,
                new House(1, "namee"), new User("usernamee"), true);
        when(requestRepository.findById(requestIdMock)).thenReturn(Optional.of(request));

        // run the test
        final ResponseEntity<Request> result = requestController.getRequestById(requestIdMock);
        assertEquals(result.getStatusCode(), HttpStatus.OK);

        // verify the results
        verify(requestRepository).findById(requestIdMock);
        assertEquals(request, result.getBody());
    }

    @Test
    public void testAddRequest() {
        // set up the request
        final Request newRequest = new Request(requestIdMock,
                new House(1, "name"), new User("username"), false);

        // configure requestRepository.save(...).
        final Request request = new Request(requestIdMock,
                new House(1, "name"), new User("username"), false);
        when(requestRepository.save(any(Request.class))).thenReturn(request);

        // run the test
        requestController.addRequest(newRequest);

        // verify the results
        verify(requestRepository).save(request);
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

        // run the test and verify the results
        final ResponseEntity<String> result = requestController.updateRequest(requestWithNewInfo);
        verify(requestRepository, times(1)).save(requestWithNewInfo);

        final ResponseEntity<String> expected =
            new ResponseEntity<>("Request updated successfully!",
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

        when(houseRepository.findById(1)).thenReturn(house1);
        when(houseRepository.findById(2)).thenReturn(house2);
        when(userRepository.findById("Malwina")).thenReturn(Optional.of(user1));
        when(userRepository.findById("Mocha")).thenReturn(Optional.of(user2));
        when(userRepository.findById("Ina")).thenReturn(newUser);
        when(requestRepository.findById(requestId1)).thenReturn(Optional.of(request));
        //when(requestRepository.findById(requestId2)).thenReturn(Optional.of(requestWithNewInfo));

        // run the test
        final ResponseEntity<String> result = requestController.updateRequest(requestWithNewInfo);

        final ResponseEntity<String> expected = new ResponseEntity<>("Request not found!",
            HttpStatus.NOT_FOUND);

        // verify the results
        assertEquals(expected, result);
    }

    @Test
    public void testDeleteRequest() {
        // run the test
        requestController.deleteRequest(requestIdMock);

        // verify the results
        verify(requestRepository).deleteById(requestIdMock);
    }

    @Test
    public void testMembersAcceptingRequest() {
        //setting the house
        final Optional<House> house = Optional.of(new House(1, "CoolHouse"));
        final User user1 = new User("Malwina");
        final User user2 = new User("Mocha");
        final Optional<User> newUser = Optional.of(new User("Ina"));
        user1.setHouse(house.get());
        user2.setHouse(house.get());

        //setting the members of the house
        Set<User> users = new HashSet<>();
        users.add(user1);
        users.add(user2);
        house.get().setUsers(users);

        //setting the request
        final RequestId requestId = new RequestId(1, "Ina");
        final Optional<Request> request = Optional.of(new Request(requestId, house.get(),
                                                        newUser.get(), false));

        when(houseRepository.findById(1)).thenReturn(house);
        when(userRepository.findById("Malwina")).thenReturn(Optional.of(user1));
        when(userRepository.findById("Mocha")).thenReturn(Optional.of(user2));
        when(userRepository.findById("Ina")).thenReturn(newUser);
        when(requestRepository.existsById(requestId)).thenReturn(true);
        when(requestRepository.findById(requestId)).thenReturn(request);

        // run the test and verify the results
        final ResponseEntity<String> result = requestController.membersAcceptingRequest("Ina",
            1, "Mocha");
        verify(requestRepository).existsById(requestId);

        final ResponseEntity<String> expected = new ResponseEntity<>("You have successfully "
            + "accepted the user: " + request.get().getUser().getUsername(), HttpStatus.OK);

        assertEquals(expected, result);
    }

    @Test
    public void testMembersAcceptingRequestDifferentHouse() {
        //setting the 1st house
        final Optional<House> house1 = Optional.of(new House(1, "CoolHouse"));
        final User user1 = new User("Malwina");
        final User user2 = new User("Mocha");
        user1.setHouse(house1.get());
        user2.setHouse(house1.get());

        //setting the members of the 1st house
        Set<User> users1 = new HashSet<>();
        users1.add(user1);
        users1.add(user2);
        house1.get().setUsers(users1);

        //setting the 2nd house
        final Optional<House> house2 = Optional.of(new House(2, "CoolestHouse"));
        final User user3 = new User("Sleepy");
        user3.setHouse(house2.get());

        //setting the members of the 2nd house
        Set<User> users2 = new HashSet<>();
        users2.add(user3);
        house2.get().setUsers(users2);

        //setting the request
        final Optional<User> newUser = Optional.of(new User("Ina"));
        final RequestId requestId = new RequestId(1, "Ina");
        final Optional<Request> request = Optional.of(new Request(requestId, house1.get(),
            newUser.get(), false));

        when(houseRepository.findById(1)).thenReturn(house1);
        when(houseRepository.findById(2)).thenReturn(house2);
        when(userRepository.findById("Malwina")).thenReturn(Optional.of(user1));
        when(userRepository.findById("Mocha")).thenReturn(Optional.of(user2));
        when(userRepository.findById("Sleepy")).thenReturn(Optional.of(user3));
        when(userRepository.findById("Ina")).thenReturn(newUser);
        when(requestRepository.existsById(requestId)).thenReturn(true);
        when(requestRepository.findById(requestId)).thenReturn(request);

        // run the test and verify the results
        final ResponseEntity<String> result = requestController.membersAcceptingRequest("Ina",
            1, "Sleepy");
        verify(requestRepository).existsById(requestId);

        final ResponseEntity<String> expected = new ResponseEntity<>("You can't accept a user"
            + " from other household!", HttpStatus.FORBIDDEN);

        assertEquals(expected, result);
    }

    @Test
    public void testMembersAcceptingRequestUserNotFound() {
        //setting the house
        final Optional<House> house = Optional.of(new House(1, "CoolHouse"));
        final User user1 = new User("Malwina");
        final User user2 = new User("Mocha");
        final Optional<User> newUser = Optional.of(new User("Ina"));
        user1.setHouse(house.get());
        user2.setHouse(house.get());

        //setting the members of the house
        Set<User> users = new HashSet<>();
        users.add(user1);
        users.add(user2);
        house.get().setUsers(users);

        //setting the request
        final RequestId requestId = new RequestId(1, "Ina");
        final Optional<Request> request = Optional.of(new Request(requestId, house.get(),
            newUser.get(), false));

        when(houseRepository.findById(1)).thenReturn(house);
        when(userRepository.findById("Malwina")).thenReturn(Optional.of(user1));
        when(userRepository.findById("Mocha")).thenReturn(Optional.of(user2));
        when(userRepository.findById("Ina")).thenReturn(newUser);
        when(requestRepository.existsById(requestId)).thenReturn(true);
        when(requestRepository.findById(requestId)).thenReturn(request);

        // run the test and verify the results
        final ResponseEntity<String> result = requestController.membersAcceptingRequest("Ina",
            1, "Sleepy");
        verify(requestRepository).existsById(requestId);

        final ResponseEntity<String> expected = new ResponseEntity<>("The user is not found!",
            HttpStatus.NOT_FOUND);

        assertEquals(expected, result);
    }

    @Test
    public void testMembersAcceptingRequestNotFound() {
        //setting the house
        final Optional<House> house = Optional.of(new House(1, "CoolHouse"));
        final User user1 = new User("Malwina");
        final User user2 = new User("Mocha");
        final Optional<User> newUser = Optional.of(new User("Ina"));
        user1.setHouse(house.get());
        user2.setHouse(house.get());

        //setting the members of the house
        Set<User> users = new HashSet<>();
        users.add(user1);
        users.add(user2);
        house.get().setUsers(users);

        //setting the request
        final RequestId requestId = new RequestId(1, "Ina");
        final Optional<Request> request = Optional.of(new Request(requestId, house.get(),
            newUser.get(), false));

        when(houseRepository.findById(1)).thenReturn(house);
        when(userRepository.findById("Malwina")).thenReturn(Optional.of(user1));
        when(userRepository.findById("Mocha")).thenReturn(Optional.of(user2));
        when(userRepository.findById("Ina")).thenReturn(newUser);
        when(requestRepository.existsById(requestId)).thenReturn(true);
        when(requestRepository.findById(requestId)).thenReturn(request);

        // run the test
        final ResponseEntity<String> result = requestController.membersAcceptingRequest("Sleepy",
            1, "Mocha");

        final ResponseEntity<String> expected = new ResponseEntity<>("The request is not found!",
            HttpStatus.NOT_FOUND);

        // verify the results
        assertEquals(expected, result);
    }

}



