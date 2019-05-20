package it.polimi.ingsw.controller;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;
import it.polimi.ingsw.model.*;

/**
 * Test for {@link DecksHandler} class
 *
 * @author Daniele Chiappalupi
 */
public class DecksHandlerTest {

    private DecksHandler decksTest;

    /**
     * Initializes the decksHandler before every test
     */
    @Before
    public void setUp() {
        decksTest = new DecksHandler();
    }

    /**
     * Tests the weaponsOver() method, both with a positive and a negative test
     * @see DecksHandler#weaponsOver()
     */
    @Test
    public void testWeaponsOver() {
        assertFalse(decksTest.weaponsOver());
        int i = 21;
        while(i > 0) {
            decksTest.drawWeapon();
            i--;
        }
        assertTrue(decksTest.weaponsOver());
    }

    /**
     * Tests the drawWeapon() method
     * @see DecksHandler#drawWeapon()
     */
    @Test
    public void testDrawWeapon() {
        ArrayList<Weapon> weapons = decksTest.initializeWeapons();
        int i = 0;
        while(!decksTest.weaponsOver()) {
            Weapon w1 = decksTest.drawWeapon();
            i++;
            assertTrue(weapons.contains(w1));
            if (!decksTest.weaponsOver()) {
                Weapon w2 = decksTest.drawWeapon();
                i++;
                assertTrue(weapons.contains(w2));
                assertNotEquals(w1, w2);
            }
        }
        assertEquals(weapons.size(), i);
        Weapon w3 = new SimpleWeapon();
        ArrayList<Weapon> weapons1 = new ArrayList<>();
        weapons1.add(w3);
        decksTest = new DecksHandler();
        assertFalse(weapons1.contains(decksTest.drawWeapon()));
    }

    /**
     * Tests a RuntimeException
     */
    @Test (expected = RuntimeException.class)
    public void testDrawWeaponException() {
        while(!decksTest.weaponsOver()) decksTest.drawWeapon();
        decksTest.drawWeapon();
    }

    /**
     * Tests the drawAmmoTile() method
     * @see DecksHandler#drawAmmoTile()
     */
    @Test
    public void testDrawAmmoTile() {
        ArrayList<AmmoTile> ammoTiles = decksTest.initializeAmmoTiles();
        int i = 0;
        while(i < 36) {
            AmmoTile a1 = decksTest.drawAmmoTile();
            i++;
            assertTrue(ammoTiles.contains(a1));
            AmmoTile a2 = decksTest.drawAmmoTile();
            i++;
            assertTrue(ammoTiles.contains(a2));
            assertNotEquals(a1, a2);
        }
    }

    /**
     * Tests the drawPowerup() method
     * @see DecksHandler#drawPowerup()
     */
    @Test
    public void testDrawPowerup() {
        ArrayList<Powerup> ammoTiles = decksTest.initializePowerups();
        int i = 0;
        while(i < 24) {
            Powerup p1 = decksTest.drawPowerup();
            i++;
            assertTrue(ammoTiles.contains(p1));
            Powerup p2 = decksTest.drawPowerup();
            i++;
            assertTrue(ammoTiles.contains(p2));
            assertNotEquals(p1, p2);
        }
    }

    /**
     * Tests both the wasteAmmoTile() method and the recycleAmmos() method, as the latter is private and it is used by the former
     * @see DecksHandler#wasteAmmoTile(AmmoTile)
     */
    @Test
    public void testWasteAmmoTileRecycleAmmos() {
        ArrayList<AmmoTile> wasted = new ArrayList<>();
        wasted.add(decksTest.drawAmmoTile());
        wasted.add(decksTest.drawAmmoTile());
        wasted.add(decksTest.drawAmmoTile());
        decksTest.wasteAmmoTile(wasted.get(0));
        decksTest.wasteAmmoTile(wasted.get(1));
        decksTest.wasteAmmoTile(wasted.get(2));
        int i = 0;
        while (i < 33) {
            assertFalse(wasted.contains(decksTest.drawAmmoTile()));
            i++;
        }
        i=0;
        while(i < 3) {
            assertTrue(wasted.contains(decksTest.drawAmmoTile()));
            i++;
        }
    }

    /**
     * Tests a NullPointerException
     */
    @Test (expected = NullPointerException.class)
    public void testWasteAmmoTileException() {
        decksTest.wasteAmmoTile(null);
    }

    /**
     * Tests both the wastePowerup() method and the recyclePowerups() method, as the latter is private and it is used by the former
     * @see DecksHandler#wastePowerup(Powerup)
     */
    @Test
    public void testWastePowerupRecyclePowerup() {
        ArrayList<Powerup> wasted = new ArrayList<>();
        wasted.add(decksTest.drawPowerup());
        wasted.add(decksTest.drawPowerup());
        wasted.add(decksTest.drawPowerup());
        decksTest.wastePowerup(wasted.get(0));
        decksTest.wastePowerup(wasted.get(1));
        decksTest.wastePowerup(wasted.get(2));
        int i = 0;
        while (i < 21) {
            assertFalse(wasted.contains(decksTest.drawPowerup()));
            i++;
        }
        i=0;
        while(i < 3) {
            assertTrue(wasted.contains(decksTest.drawPowerup()));
            i++;
        }
    }

    /**
     * Tests a NullPointerException
     */
    @Test (expected = NullPointerException.class)
    public void testWastePowerupException() {
        decksTest.wastePowerup(null);
    }
}
