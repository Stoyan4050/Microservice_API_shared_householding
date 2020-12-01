package nl.tudelft.sem.template.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.entities.House;
import nl.tudelft.sem.template.entities.Request;
import nl.tudelft.sem.template.entities.RequestId;
import nl.tudelft.sem.template.entities.User;
import nl.tudelft.sem.template.repositories.RequestRepository;
//import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

/**
 * Tests for the RequestController class and RequestRepository.
 */
@SuppressWarnings("PMD")
public class RequestControllerTest {

    @Mock
    private transient RequestRepository mockRepository;

    @Mock
    private static RequestId requestIdMock;

    @InjectMocks
    private transient RequestController requestControllerUnderTest;

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
        when(mockRepository.findAll()).thenReturn(requests);

        // Run the test
        final List<Request> result = requestControllerUnderTest.getAllRequests();

        // Verify the results
    }


    @Test
    public void testGetRequestById() {
        // Setup

        // Configure RequestRepository.findById(...).
        final Optional<Request> requests = Optional.of(new Request(requestIdMock,
                new House(1, "namee"), new User("usernamee"), true));
        when(mockRepository.findById(requestIdMock)).thenReturn(requests);

        // Run the test
        final Optional<Request> result = requestControllerUnderTest.getRequestById(requestIdMock);

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
        when(mockRepository.save(any(Request.class))).thenReturn(request);

        // Run the test
        requestControllerUnderTest.addRequest(newRequest);

        // Verify the results
    }

    @Test
    public void testRemoveRequest() {
        // Setup

        // Run the test
        requestControllerUnderTest.removeRequest(requestIdMock);

        // Verify the results
        verify(mockRepository).deleteById(requestIdMock);
    }

}

