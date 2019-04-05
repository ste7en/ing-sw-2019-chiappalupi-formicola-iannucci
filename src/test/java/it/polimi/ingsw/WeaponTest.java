package it.polimi.ingsw;

import org.junit.*;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Test for {@link Weapon} class
 *
 * @author Daniele Chiappalupi
 */
public class WeaponTest {

    private Weapon weaponTester;
    private static ArrayList<AmmoColor> cost = new ArrayList<>();
    private static ArrayList<Effect> effects = new ArrayList<>();

    /**
     * Initializes the attributes for the test class with sample values
     */
    @BeforeClass
    public static void setUpBeforeClass() {
        cost.add(AmmoColor.red);
        cost.add(AmmoColor.blue);
        cost.add(AmmoColor.yellow);
        Effect e = new Effect("test", null, EffectType.Basic);
        effects.add(e);
    }

    /**
     * Initializes the weaponTester before every testG
     */
    @Before
    public void setUp() {
        weaponTester = new Weapon("weaponTester", cost, "this is a test Weapon", WeaponType.SimpleWeapon, effects);
    }

    /**
     * Tests the getType() method, both with a positive and a negative test
     * @see Weapon#getType()
     */
    @Test
    public void testGetType() {
        assertNotNull(weaponTester.getType());
        assertEquals(WeaponType.SimpleWeapon, weaponTester.getType());
        assertNotEquals(WeaponType.PotentiableWeapon, weaponTester.getType());
    }

    /**
     * Tests the getName() method, both with a positive and a negative test
     * @see Weapon#getName()
     */
    @Test
    public void testGetName() {
        assertNotNull(weaponTester.getName());
        assertEquals("weaponTester", weaponTester.getName());
        assertNotEquals("weapon1", weaponTester.getName());
    }

    /**
     * Tests the getCost() method, both with a positive and a negative test
     * @see Weapon#getCost()
     */
    @Test
    public void testGetCost() {
        assertNotNull(weaponTester.getCost());
        ArrayList<AmmoColor> cost = new ArrayList<>();
        cost.add(AmmoColor.red);
        cost.add(AmmoColor.blue);
        cost.add(AmmoColor.yellow);
        assertEquals(cost, weaponTester.getCost());
        cost.add(AmmoColor.red);
        assertNotEquals(cost, weaponTester.getCost());
    }

    /**
     * Tests the getNotes() method, both with a positive and a negative test
     * @see Weapon#getNotes()
     */
    @Test
    public void testGetNotes() {
        assertNotNull(weaponTester.getNotes());
        assertEquals("this is a test Weapon", weaponTester.getNotes());
        assertNotEquals("this is the armageddon", weaponTester.getNotes());
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

    /**
     * Tests the getEffect() method, both with positives and negatives tests
     * @see Weapon#getEffect()
     */
    @Test
    public void testGetEffect() {
        assertNotNull(weaponTester.getEffect());
        ArrayList<Effect> effects = new ArrayList<>();
        Effect e = new Effect("test", null, EffectType.Basic);
        effects.add(e);
        for(Effect effect : effects) {
            assertEquals(effect.getName(), weaponTester.getEffect().get(effects.indexOf(effect)).getName());
            assertEquals(effect.getCost(), weaponTester.getEffect().get(effects.indexOf(effect)).getCost());
            assertEquals(effect.getType(), weaponTester.getEffect().get(effects.indexOf(effect)).getType());
        }
        Effect e1 = new Effect("failtest", null, EffectType.Additional);
        effects.add(e1);
        for(Effect effect : effects) {
            if(effects.indexOf(effect) > weaponTester.getEffect().size()) {
                assertNotEquals(effect.getName(), weaponTester.getEffect().get(effects.indexOf(effect)).getName());
                assertNotEquals(effect.getCost(), weaponTester.getEffect().get(effects.indexOf(effect)).getCost());
                assertNotEquals(effect.getType(), weaponTester.getEffect().get(effects.indexOf(effect)).getType());
            }
        }
    }
}
