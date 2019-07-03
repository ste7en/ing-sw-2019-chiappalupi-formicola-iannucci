package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.Effect;
import it.polimi.ingsw.model.cards.Weapon;
import it.polimi.ingsw.model.utility.AmmoColor;
import org.junit.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import static org.junit.Assert.*;
import it.polimi.ingsw.controller.*;

/**
 * Test for {@link Effect} class
 *
 * @author Daniele Chiappalupi
 */
public class EffectTest {

    private Effect effectTester;
    private static DecksHandler decks;
    private static String name;
    private static String description;
    private static Map<AmmoColor, Integer> cost;

    /**
     * Randomly initializes the attributes for the test class
     */
    @BeforeClass
    public static void setUpBeforeClass() {
        decks = new DecksHandler();
    }

    /**
     * Initializes the effectTester and its attributes before every test
     */
    @Before
    public void setUp() {
        Random r = new Random();
        Weapon w = decks.drawWeapon();
        effectTester = w.getEffects().get(r.nextInt(w.getEffects().size()));
        name = effectTester.getName();
        //cost = effectTester.getCost();
        description = effectTester.getDescription();
    }

    /**
     * Tests the name getter, both with a positive and a negative test
     * @see Effect#getName()
     */
    @Test
    public void testGetName() {
        assertNotNull(effectTester.getName());
        assertEquals(name, effectTester.getName());
        assertNotEquals("wrongName", effectTester.getName());
    }

}
