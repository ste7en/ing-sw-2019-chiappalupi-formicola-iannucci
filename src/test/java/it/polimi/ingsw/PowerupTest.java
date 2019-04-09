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
    private static PowerupType powType;
    private static String powDescription;
    private static AmmoColor powColor;

    /**
     * Initializes the attributes for the test class with sample values
     */
    @BeforeClass
    public static void setUpBeforeClass() {
        powType = PowerupType.values()[new Random().nextInt(4)];
        powDescription = "This is a sample description";
        powColor = AmmoColor.values()[new Random().nextInt(3)];
    }

    /**
     * Initializes the powTester before every test
     */
    @Before
    public void setUp() {
        powTester = new Powerup(powType, powDescription, powColor);
    }

    /**
     * Tests the getType() method, both with a positive and a negative test
     * @see Powerup#getType()
     */
    @Test
    public void testGetType() {
        assertNotNull(powTester.getType());
        assertEquals(powType, powTester.getType());
        assertNotEquals((powType == PowerupType.Newton) ? PowerupType.TagbackGrenade : PowerupType.Newton, powTester.getType());
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

    /**
     * Tests a NullPointerException
     */
    @Test (expected = NullPointerException.class)
    public void testUseNullPointerException() {
        powTester.use(null, null, null);
    }

    /**
     * Tests a RuntimeException
     */
    @Test (expected = RuntimeException.class)
    public void testUseRuntimeException() {
        Player playerTest;
        User userTest;
        Character characterTest;
        String username = "Mimmo";
        userTest = new User(username);
        characterTest = new Character("CharacterTestName", PlayerColor.yellow, "Character user for tests.");
        playerTest = new Player(userTest, characterTest);
        powTester.use(playerTest, playerTest, null);
    }
}
