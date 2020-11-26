package nl.tudelft.sem.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import nl.tudelft.sem.auth.Example;
import org.junit.jupiter.api.Test;

@SuppressWarnings("PMD")
public class ExampleTest {

    @Test
    public void testGettersSetters() {
        final Example exampleTest = new Example("name");
        assertEquals("name", exampleTest.getName(), "name not equal");
    }

    @Test
    public void testGettersSettersTwo() {
        final Example exampleTest = new Example("name");

        exampleTest.setName("newName");
        assertEquals("newName", exampleTest.getName(), "new name not equal");
    }
}
