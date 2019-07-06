package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.DecksHandler;
import it.polimi.ingsw.model.cards.AmmoTile;
import org.junit.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    @Test
    public void fakeTest() {
        String s1 = "Ammo Card: powerup, yellow ammo, red ammo; [Cell -> (1, 3)]";
        String s2 = "Sledgehammer [Cell -> (2, 3)]";
        String s4 = s1.substring(0, 9);
        String s3 = s2.substring(s2.length() - 6, s2.length() - 2);
        s3 = s3.replaceAll(",", "");
        List<Integer> x = Arrays.stream(s3.split("\\s"))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        System.out.println(x);
        assertEquals("Ammo Card", s4);
        System.out.println(s2.substring(0, s2.length() - 17));

    }
}
