package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.DecksHandler;
import it.polimi.ingsw.model.cards.Powerup;
import it.polimi.ingsw.model.player.PlayerHand;
import it.polimi.ingsw.model.utility.AmmoColor;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test for {@link PlayerHand} class, that
 * manages every deck of cards a player can use.
 *
 * @author Stefano Formicola
 */
public class PlayerHandTest {

    private PlayerHand playerHand;
    private static AmmoColor testColor;
    private static Integer testAmount;
    private static AmmoColor testAbsentColor;
    private static Powerup testPowerup;

    /**
     * Initializes the needed attributes for the test class
     */
    @BeforeClass
    public static void setUpTestClass() {
        testColor = AmmoColor.yellow;
        testAmount = 3;
        testAbsentColor = AmmoColor.blue;
        DecksHandler dh = new DecksHandler();
        testPowerup = dh.drawPowerup();
    }

    /**
     * Initializes the playerHand before each test
     */
    @Before
    public void setUp() {
        playerHand = new PlayerHand();
    }

    /**
     * Tests the constructor of the {@link PlayerHand} class
     * @see PlayerHand#PlayerHand()
     */
    @Test
    public void testConstructor() {
        assertNotNull(playerHand);
    }

    /**
     * Tests a NullPointerException
     */
    @Test (expected = NullPointerException.class)
    public void setWeapons() {
        playerHand.setWeapons(null);
    }

    /**
     * Getter test
     */
    @Test
    public void getWeapons() {
        assertNotNull(playerHand.getWeapons());
    }

    /**
     * Getter test
     */
    @Test
    public void getPowerups() {
        assertNotNull(playerHand.getPowerups());
    }

    /**
     * Getter test
     */
    @Test
    public void addAndGetPowerups() {
        playerHand.addPowerup(testPowerup);
        assertNotNull(playerHand.getPowerups());
        playerHand.addPowerup(testPowerup);

        assertEquals(2, playerHand.getPowerups().size());
    }

    /**
     * Tests the impossibility of adding more powerups than the allowed number
     */
    @Test (expected = RuntimeException.class)
    public void testMaximumNumberOfPowerups() {
        playerHand.addPowerup(testPowerup);
        playerHand.addPowerup(testPowerup);
        playerHand.addPowerup(testPowerup);

        playerHand.addPowerup(testPowerup);
    }

    /**
     * Tests a NullPointerException
     */
    @Test (expected = NullPointerException.class)
    public void addPowerup() {
        playerHand.addPowerup(null);
    }

    /**
     * Tests the correct amount of added ammos
     */
    @Test
    public void getAmmosAmount() {
        assertEquals(playerHand.getAmmosAmount(testColor), (Integer) 0);
        playerHand.updateAmmos(testColor, testAmount);
        assertEquals(playerHand.getAmmosAmount(testAbsentColor), (Integer)0);
    }

    /**
     * Tests the correct update of ammos
     */
    @Test
    public void updateAmmos() {
        playerHand.updateAmmos(testColor, testAmount);
        assertEquals(playerHand.getAmmosAmount(testColor), testAmount);
    }

    /**
     * Tests the impossibility of adding more ammos than the allowed number
     */
    @Test (expected = RuntimeException.class)
    public void testMaximumNumberOfCubes() {
        playerHand.updateAmmos(testColor, testAmount);
        playerHand.updateAmmos(testColor, testAmount);
    }

    /**
     * Tests the impossibility of adding more ammos than the allowed number
     */
    @Test (expected = IllegalArgumentException.class)
    public void testInvalidAmountOfCubesToIncrement() {
        playerHand.updateAmmos(AmmoColor.red, 5);
    }

    /**
     * Tests the impossibility of decrementing the number of ammos to a negative
     */
    @Test (expected = IllegalArgumentException.class)
    public void testInvalidAmountOfCubesToDecrement() {
        playerHand.updateAmmos(testColor, -2);
    }

    /**
     * Tests the correctness of arguments when adding cubes of a certain color
     */
    @Test (expected = IllegalArgumentException.class)
    public void testInvalidColorException() {
        playerHand.updateAmmos(AmmoColor.none, 2);
    }
}