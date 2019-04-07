package it.polimi.ingsw;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.Random;

/**
 * Test for {@link Powerup} class
 *
 * @author Daniele Chiappalupi
 */
public class PowerupTest {

    private Powerup powTester;
    private static String powName;
    private static String powDescription;
    private static AmmoColor powColor;

    /**
     * Initializes the attributes for the test class with sample values
     */
    @BeforeClass
    public static void setUpBeforeClass() {
        powName = "powTester";
        powDescription = "This is a sample description";
        powColor = AmmoColor.values()[new Random().nextInt(3)];
    }

    /**
     * Initializes the powTester before every test
     */
    @Before
    public void setUp() {
        powTester = new Powerup(powName, powDescription, powColor);
    }

    /**
     * Tests the getName() method, both with a positive and a negative test
     * @see Powerup#getName()
     */
    @Test
    public void testGetName() {
        assertNotNull(powTester.getName());
        assertEquals(powName, powTester.getName());
        assertNotEquals("This is a fake name", powTester.getName());
    }

    /**
     * Tests the getDescription() method, both with a positive and a negative test
     * @see Powerup#getDescription()
     */
    @Test
    public void testGetDescription() {
        assertNotNull(powTester.getDescription());
        assertEquals(powDescription, powTester.getDescription());
        assertNotEquals("This is a fake description", powTester.getDescription());
    }

    /**
     * Tests the getColor() method, both with a positive and a negative test
     * @see Powerup#getColor()
     */
    @Test
    public void testGetColor() {
        assertNotNull(powTester.getColor());
        assertEquals(powColor, powTester.getColor());
        assertNotEquals(AmmoColor.blue==powColor ? AmmoColor.red : AmmoColor.blue, powTester.getColor());
    }
}
