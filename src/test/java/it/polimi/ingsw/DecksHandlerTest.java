package it.polimi.ingsw;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

/**
 * Test for {@link DecksHandler} class
 *
 * @author Daniele Chiappalupi
 */
public class DecksHandlerTest {

    private DecksHandler decksTest;
    private static ArrayList<Weapon> weapons = new ArrayList<>();
    private static ArrayList<AmmoTile> ammoTiles  = new ArrayList<>();
    private static ArrayList<Powerup> powerups = new ArrayList<>();

    /**
     * Initializes the attributes for the test class with random values
     */
    @BeforeClass
    public static void setUpBeforeClass() {
        Random rand = new Random();
        int size = rand.nextInt(20) + 1;
        for (int i=0; i < size; i++) {
            WeaponType type = WeaponType.values()[new Random().nextInt(3)];
            weapons.add(new Weapon("TestWeapon"+i, new ArrayList<>(), "TestNotes"+i, type, new ArrayList<>()));
        }

        size = rand.nextInt(20) + 1;
        for (int i=0; i < size; i++) {
            ArrayList<AmmoColor> colors = new ArrayList<>();
            int costSize = new Random().nextInt(3);
            for(int j=0; j < costSize; j++) colors.add(AmmoColor.values()[new Random().nextInt(3)]);
            ammoTiles.add(new AmmoTile(colors, new Random().nextBoolean() ? null : new Powerup("name", "description", AmmoColor.values()[new Random().nextInt(3)])));
        }

        size = rand.nextInt(20) + 1;
        for (int i=0; i < size; i++) {
            powerups.add(new Powerup("TestPowerup"+i, "TestDescription"+i, AmmoColor.values()[new Random().nextInt(3)]));
        }
    }

    /**
     * Initializes the decksHandler before every test
     */
    @Before
    public void setUp() {
        decksTest = new DecksHandler((ArrayList<Weapon>)weapons.clone(), (ArrayList<AmmoTile>)ammoTiles.clone(), (ArrayList<Powerup>)powerups.clone());
    }

    /**
     * Tests the weaponsOver() method, both with a positive and a negative test
     * @see DecksHandler#weaponsOver()
     */
    @Test
    public void testWeaponsOver() {
        assertFalse(decksTest.weaponsOver());
        decksTest = new DecksHandler(new ArrayList<>(), ammoTiles, powerups);
        assertTrue(decksTest.weaponsOver());
    }

    /**
     * Tests the drawWeapon() method
     * @see DecksHandler#drawWeapon()
     */
    @Test
    public void testDrawWeapon() {
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
        assertEquals(weapons.size(),i);
        Weapon w3 = new Weapon("weaponNotContained", new ArrayList<>(), "notes", WeaponType.PotentiableWeapon, new ArrayList<>());
        ArrayList<Weapon> weapons1 = new ArrayList<>();
        weapons1.add(w3);
        decksTest = new DecksHandler(weapons1, ammoTiles, powerups);
        assertFalse(weapons.contains(decksTest.drawWeapon()));
    }

    /**
     * Tests the drawAmmoTile() method
     * @see DecksHandler#drawAmmoTile()
     */
    @Test
    public void testDrawAmmoTile() {
        int i = 0;
        AmmoTile a = new AmmoTile(new ArrayList<>(), null);
        decksTest.wasteAmmoTile(a);
        while (i < ammoTiles.size()) {
            assertTrue(ammoTiles.contains(decksTest.drawAmmoTile()));
            i++;
        }
        decksTest.wasteAmmoTile(a);
        assertEquals(a, decksTest.drawAmmoTile());
        decksTest.wasteAmmoTile(a);
        assertFalse(ammoTiles.contains(decksTest.drawAmmoTile()));
        assertEquals(i, ammoTiles.size());
    }

    /**
     * Tests the drawPowerup() method
     * @see DecksHandler#drawPowerup()
     */
    @Test
    public void testDrawPowerup() {
        int i = 0;
        Powerup p = new Powerup("toWaste", "Needed to avoid any NullPointerException", AmmoColor.red);
        decksTest.wastePowerup(p);
        while (i < powerups.size()) {
            assertTrue(powerups.contains(decksTest.drawPowerup()));
            i++;
        }
        decksTest.wastePowerup(p);
        assertEquals(p, decksTest.drawPowerup());
        decksTest.wastePowerup(p);
        assertFalse(powerups.contains(p));
        assertEquals(i, powerups.size());
    }

    /**
     * Tests both the wasteAmmoTile() method and the recycleAmmos() method, as the latter is private and it is used by the former
     * @see DecksHandler#wasteAmmoTile(AmmoTile)
     */
    @Test
    public void testWasteAmmoTileRecycleAmmos() {
        AmmoTile a1 = new AmmoTile(null, null);
        AmmoTile a2 = new AmmoTile(null,  new Powerup("wasted2", "testing2", AmmoColor.blue));
        AmmoTile a3 = new AmmoTile(null, new Powerup("wasted3", "testing3", AmmoColor.yellow));
        ArrayList<AmmoTile> wasted = new ArrayList<>();
        wasted.add(a1);
        wasted.add(a2);
        wasted.add(a3);
        decksTest.wasteAmmoTile(a1);
        decksTest.wasteAmmoTile(a2);
        decksTest.wasteAmmoTile(a3);
        int i = 0;
        while (i < ammoTiles.size()) {
            assertFalse(wasted.contains(decksTest.drawAmmoTile()));
            i++;
        }
        decksTest.wasteAmmoTile(new AmmoTile(null, new Powerup("wasted", "testing", AmmoColor.red)));
        i=0;
        while(i < 3) {
            assertTrue(wasted.contains(decksTest.drawAmmoTile()));
            i++;
        }
        decksTest.wasteAmmoTile(a1);
        assertFalse(wasted.contains(decksTest.drawAmmoTile()));
    }

    /**
     * Tests both the wastePowerup() method and the recyclePowerups() method, as the latter is private and it is used by the former
     * @see DecksHandler#wastePowerup(Powerup)
     */
    @Test
    public void testWastePowerup() {
        Powerup p1 = new Powerup("wasted1", "testing1", AmmoColor.red);
        Powerup p2 = new Powerup("wasted2", "testing2", AmmoColor.blue);
        Powerup p3 = new Powerup("wasted3", "testing3", AmmoColor.yellow);
        ArrayList<Powerup> wasted = new ArrayList<>();
        wasted.add(p1);
        wasted.add(p2);
        wasted.add(p3);
        decksTest.wastePowerup(p1);
        decksTest.wastePowerup(p2);
        decksTest.wastePowerup(p3);
        int i = 0;
        while (i < powerups.size()) {
            assertFalse(wasted.contains(decksTest.drawPowerup()));
            i++;
        }
        decksTest.wastePowerup(new Powerup("wasted", "testing", AmmoColor.red));
        i=0;
        while(i < 3) {
            assertTrue(wasted.contains(decksTest.drawPowerup()));
            i++;
        }
        decksTest.wastePowerup(p1);
        assertFalse(wasted.contains(decksTest.drawPowerup()));
    }

}
