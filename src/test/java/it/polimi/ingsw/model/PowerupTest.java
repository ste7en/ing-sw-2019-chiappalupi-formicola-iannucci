package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.DecksHandler;
import org.junit.*;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Test for {@link Powerup} class
 *
 * @author Daniele Chiappalupi
 */
public class PowerupTest {

    private Powerup powTester;

    private static DecksHandler dh;

    /**
     * Initializes the attributes for the test class with sample values
     */
    @BeforeClass
    public static void setUpBeforeClass() {
        dh = new DecksHandler();
    }

    /**
     * Initializes the powTester before every test
     */
    @Before
    public void setUp() {
        powTester = dh.drawPowerup();
    }

    /**
     * Tests the getType() method, both with a positive and a negative test
     * @see Powerup#getType()
     */
    @Test
    public void testGetType() {
        assertNotNull(powTester.getType());
        ArrayList<PowerupType> powerupTypes = new ArrayList<>(Arrays.asList(PowerupType.values()));
        int i = 0;
        while(i < 24) {
            powTester = dh.drawPowerup();
            assertTrue(powerupTypes.contains(powTester.getType()));
            dh.wastePowerup(powTester);
            i++;
        }
    }

    /**
     * Tests the getDescription() method, both with a positive and a negative test
     * @see Powerup#getDescription()
     */
    @Test
    public void testGetDescription() {
        assertNotNull(powTester.getDescription());
        assertNotEquals("This is a fake description", powTester.getDescription());
    }

    /**
     * Tests the getColor() method, both with a positive and a negative test
     * @see Powerup#getColor()
     */
    @Test
    public void testGetColor() {
        assertNotNull(powTester.getColor());
        ArrayList<AmmoColor> ammoColors = new ArrayList<>(Arrays.asList(AmmoColor.values()));
        int i = 0;
        while(i < 24) {
            powTester = dh.drawPowerup();
            assertTrue(ammoColors.contains(powTester.getColor()));
            dh.wastePowerup(powTester);
            i++;
        }
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
