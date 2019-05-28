package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.DecksHandler;
import it.polimi.ingsw.model.cards.AmmoTile;
import org.junit.*;

import static org.junit.Assert.*;

/**
 * Test for {@link AmmoTile} class
 *
 * @author Daniele Chiappalupi
 */
public class AmmoTileTest {

    private AmmoTile ammoTester;
    private static DecksHandler dh;

    /**
     * Randomly initializes the attributes for the test class
     */
    @BeforeClass
    public static void setUpBeforeClass() {
        dh = new DecksHandler();
    }

    /**
     * Initializes the ammoTester before every test
     */
    @Before
    public void setUp() {
        ammoTester = dh.drawAmmoTile();
    }

    /**
     * Tests the hasPowerup() method, both in a positive and a negative case
     * @see AmmoTile#hasPowerup()
     */
    @Test
    public void testHasPowerup() {
        int i = 0;
        while(i < 36) {
            assertNotNull(ammoTester.hasPowerup());
            assertTrue(ammoTester.getAmmoColors().size() == 2 ? ammoTester.hasPowerup() : !ammoTester.hasPowerup());
            i++;
            dh.wasteAmmoTile(ammoTester);
            dh.drawAmmoTile();
        }
    }

    /**
     * Tests the AmmoColors getter, both with a positive and a negative case
     * @see AmmoTile#getAmmoColors()
     */
    @Test
    public void testGetAmmoColors() {
        int i = 0;
        while(i < 36) {
            assertNotNull(ammoTester.getAmmoColors());
            assertTrue(ammoTester.getAmmoColors().size() == 2 || ammoTester.getAmmoColors().size() == 3);
            i++;
            dh.wasteAmmoTile(ammoTester);
            dh.drawAmmoTile();
        }
    }
}
