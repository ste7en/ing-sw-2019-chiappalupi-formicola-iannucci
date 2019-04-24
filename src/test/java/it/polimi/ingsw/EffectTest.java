package it.polimi.ingsw;

import org.junit.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import static org.junit.Assert.*;

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
    private static HashMap<AmmoColor, Integer> cost;
    private static EffectType type;

    /**
     * Randomly initializes the attributes for the test class
     */
    @BeforeClass
    public static void setUpBeforeClass() {
        decks = new DecksHandler(new ArrayList<>(), new ArrayList<>());
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
        cost = effectTester.getCost();
        description = effectTester.getDescription();
        type = effectTester.getType();
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

    /**
     * Tests the type getter, both with a positive and a negative test
     * @see Effect#getType()
     */
    @Test
    public void testGetType() {
        assertNotNull(effectTester.getType());
        assertEquals(type, effectTester.getType());
        assertNotEquals(type == EffectType.Additional ? EffectType.Basic : EffectType.Additional, effectTester.getType());
    }

    /**
     * Tests the actions adder and getter, both with positives and negatives test
     * @see Effect#addAction(Action)
     * @see Effect#getActions()
     */
    @Test
    public void testAddAction() {
        //assertNotNull(effectTester.getActions());
        ArrayList<Action> verifier = new ArrayList<>();
        Action a = (p, m) -> {
            //sample code
        };
        effectTester.addAction(a);
        assertEquals(1, effectTester.getActions().size());
        assertFalse(verifier.contains(effectTester.getActions().get(0)));
        verifier.add(a);
        assertTrue(verifier.contains(effectTester.getActions().get(0)));
    }

    /*
    /**
     * Tests a NullPointerException
     */
    /*@Test (expected = NullPointerException.class)
    public void testAddActionException() {
        effectTester.addAction(null);
    }*/

}
