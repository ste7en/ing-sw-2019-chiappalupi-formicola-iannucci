package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.DecksHandler;
import it.polimi.ingsw.model.cards.Powerup;
import it.polimi.ingsw.model.utility.AmmoColor;
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
}
