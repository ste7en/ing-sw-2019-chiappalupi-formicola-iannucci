package it.polimi.ingsw;

import org.junit.*;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class WeaponTest {

    Weapon weaponTester;

    @Before
    public void setUp() {
        ArrayList<AmmoColor> cost = new ArrayList<>();
        cost.add(AmmoColor.red);
        cost.add(AmmoColor.blue);
        cost.add(AmmoColor.yellow);
        ArrayList<Effect> effects = new ArrayList<>();
        Effect e = new Effect("test", null, EffectType.Basic);
        effects.add(e);
        weaponTester = new Weapon("weaponTester", cost, "this is a test Weapon", true, WeaponType.SimpleWeapon, effects);
    }

    @Test
    public void testGetName() {
        assertNotNull(weaponTester.getName());
        assertEquals("weaponTester", weaponTester.getName());
        assertNotEquals("weapon1", weaponTester.getName());
    }

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

    @Test
    public void testGetNotes() {
        assertNotNull(weaponTester.getNotes());
        assertEquals("this is a test Weapon", weaponTester.getNotes());
        assertNotEquals("this is the armageddon", weaponTester.getNotes());
    }

    @Test
    public void testIsLoaded() {
        weaponTester.reload();
        assertTrue(weaponTester.isLoaded());
        weaponTester.unload();
        assertFalse(weaponTester.isLoaded());
        weaponTester.reload();
        assertTrue(weaponTester.isLoaded());
    }

    @Test
    public void testUnload() {
        weaponTester.unload();
        assertFalse(weaponTester.isLoaded());
    }

    @Test
    public void testReload() {
        weaponTester.reload();
        assertTrue(weaponTester.isLoaded());
    }

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
