package nl.tudelft.sem.template.entities;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the RequestId composite key class.
 */
public class RequestIdTest {

    private static RequestId requestIdUnderTest;

    @BeforeEach
    public void setUp() {
        requestIdUnderTest = new RequestId(1, "username");
    }

    @Test
    public void testEqualsFalse() {

        RequestId toCompare = new RequestId(1, "anotherUsername");
        final boolean result = requestIdUnderTest.equals(toCompare);
        assertFalse(result);
    }

    @Test
    public void testEqualsTrue() {

        RequestId toCompare = new RequestId(1, "username");
        final boolean result = requestIdUnderTest.equals(toCompare);
        assertTrue(result);
    }

    @Test
    public void testGetHouseNr() {
        final boolean result = requestIdUnderTest.getHouseNr() == 1;
        assertTrue(result);
    }

    @Test
    public void testSetHouseNr() {
        requestIdUnderTest.setHouseNr(5);
        final boolean result = requestIdUnderTest.getHouseNr() == 5;
        Assertions.assertTrue(result);
    }

    @Test
    public void testGetUsername() {
        final boolean result = requestIdUnderTest.getUsername().equals("username");
        assertTrue(result);
    }

    @Test
    public void testSetUsername() {
        requestIdUnderTest.setUsername("username2");
        final boolean result = requestIdUnderTest.getUsername().equals("username2");
        Assertions.assertTrue(result);
    }

}