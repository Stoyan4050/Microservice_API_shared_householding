package nl.tudelft.sem.template.entities;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class RequestTest {

    @Mock
    private static RequestId requestIdMock;

    @Mock
    private static House mockHouse;

    @Mock
    private static User mockUser;

    private static Request requestUnderTest;


    /**
     * JavaDoc.
     */
    @Before
    public void setUp() {
        initMocks(this);

        when(requestIdMock.getHouseNr()).thenReturn(5);
        when(requestIdMock.getUsername()).thenReturn("UserName");

        when(mockHouse.equals(new Object())).thenReturn(true);
        when(mockUser.equals(new Object())).thenReturn(true);

    }

    @BeforeEach
    public void setUpAll() {
        requestUnderTest = new Request(requestIdMock, mockHouse, mockUser, false);
    }

    @Test
    public void constructorEqualsTest() {

        Assertions.assertEquals(requestUnderTest,
                new Request(requestIdMock, mockHouse, mockUser, false));
    }

    @Test
    public void constructorNotEqualsTest() {

        Assertions.assertNotEquals(requestUnderTest,
                new Request(requestIdMock, mockHouse, mockUser, true));
    }

    @Test
    public void testIdAndGetId() {
        Assertions.assertEquals(requestUnderTest.getId(), requestIdMock);
    }

    @Test
    public void testIdAndSetId() {
        requestUnderTest.setId(requestIdMock);
        Assertions.assertEquals(requestUnderTest.getId(), requestIdMock);
    }

    @Test
    public void testHouseAndGetHouse() {
        Assertions.assertEquals(requestUnderTest.getHouse(), mockHouse);
    }

    @Test
    public void testHouseAndSetHouse() {
        requestUnderTest.setHouse(mockHouse);
        Assertions.assertEquals(requestUnderTest.getHouse(), mockHouse);
    }

    @Test
    public void testUserAndGetUser() {
        Assertions.assertEquals(requestUnderTest.getUser(), mockUser);
    }

    @Test
    public void testUserAndSetUser() {
        requestUnderTest.setUser(mockUser);
        Assertions.assertEquals(requestUnderTest.getUser(), mockUser);
    }

    @Test
    public void testIsApproved() {
        final boolean result = requestUnderTest.isApproved(); //false in this case
        Assertions.assertFalse(result);
    }

    @Test
    public void testSetApproved() {
        requestUnderTest.setApproved(true);
        final boolean result = requestUnderTest.isApproved(); //true in this case
        Assertions.assertTrue(result);
    }

}

