package it.polimi.ingsw.model;

import org.junit.*;
import java.util.ArrayList;
import java.util.Random;
import static org.junit.Assert.*;

/**
 * Test for {@link AmmoTile} class
 *
 * @author Daniele Chiappalupi
 */
public class AmmoTileTest {

    private AmmoTile ammoTester;
    private static ArrayList<AmmoColor> ammos;
    private static boolean powerup;

    /**
     * Randomly initializes the attributes for the test class
     */
    @BeforeClass
    public static void setUpBeforeClass() {
        ammos = new ArrayList<>();
        int costSize = (new Random().nextBoolean()) ? 2 : 3;
        for(int j=0; j < costSize; j++) ammos.add(AmmoColor.values()[new Random().nextInt(3)]);
        powerup = (costSize == 2) ? true : false;
    }

    /**
     * Initializes the ammoTester before every test
     */
    @Before
    public void setUp() {
        ammoTester = new AmmoTile(ammos, powerup);
    }

    /**
     * Tests the hasPowerup() method, both in a positive and a negative case
     * @see AmmoTile#hasPowerup()
     */
    @Test
    public void testHasPowerup() {
        assertNotNull(ammoTester.hasPowerup());
        assertEquals(powerup, ammoTester.hasPowerup());
        assertNotEquals(powerup ? false : true, ammoTester.hasPowerup());
    }

    /**
     * Tests the AmmoColors getter, both with a positive and a negative case
     * @see AmmoTile#getAmmoColors()
     */
    @Test
    public void testGetAmmoColors() {
        assertNotNull(ammoTester.getAmmoColors());
        assertEquals(ammos, ammoTester.getAmmoColors());
        assertNotEquals(new ArrayList<AmmoColor>(), ammoTester.getAmmoColors());
    }
}
