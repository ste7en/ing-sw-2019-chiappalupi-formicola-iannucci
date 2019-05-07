package it.polimi.ingsw.model;

import org.junit.*;
import java.util.ArrayList;
import static org.junit.Assert.*;
import it.polimi.ingsw.controller.*;

/**
 * Test for {@link Weapon} class
 *
 * @author Daniele Chiappalupi
 */
public class WeaponTest {

    private Weapon weaponTester;
    private static DecksHandler dh;

    /**
     * Initializes a decks handler where to draw sample weapons
     */
    @BeforeClass
    public static void setUpBeforeClass() {
        dh = new DecksHandler(new ArrayList<>(), new ArrayList<>());
    }

    /**
     * Initializes the weaponTester before every testG
     */
    @Before
    public void setUp() {
        weaponTester = dh.drawWeapon();

    }

    /**
     * Tests the isLoaded() method, both with a positive and a negative test
     * @see Weapon#isLoaded()
     */
    @Test
    public void testIsLoaded() {
        weaponTester.reload();
        assertTrue(weaponTester.isLoaded());
        weaponTester.unload();
        assertFalse(weaponTester.isLoaded());
        weaponTester.reload();
        assertTrue(weaponTester.isLoaded());
    }

    /**
     * Tests the unload() method
     * @see Weapon#unload()
     */
    @Test
    public void testUnload() {
        weaponTester.unload();
        assertFalse(weaponTester.isLoaded());
    }

    /**
     * Tests the reload() method
     * @see Weapon#reload()
     */
    @Test
    public void testReload() {
        weaponTester.reload();
        assertTrue(weaponTester.isLoaded());
    }
}
