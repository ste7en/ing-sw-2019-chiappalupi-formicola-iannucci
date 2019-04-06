package it.polimi.ingsw;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import static it.polimi.ingsw.PlayerColor.*;
import static org.junit.Assert.*;

/**
 * Test for {@link PlayerBoard} class
 *
 * @author Stefano Formicola
 */

public class PlayerBoardTest {

    private PlayerBoard playerBoard;

    private static PlayerColor testColor;
    private static ArrayList<PlayerColor> emptyPlayerColorArray;
    private static ArrayList<PlayerColor> testPlayerColorArray;

    /**
     * Initializes the needed attributes for the test class
     */
    @BeforeClass
    public static void setUpClass() {
        testColor = yellow;
        emptyPlayerColorArray = new ArrayList<PlayerColor>();

        testPlayerColorArray = new ArrayList<PlayerColor>();
        testPlayerColorArray.add(green);
        testPlayerColorArray.add(blue);
        testPlayerColorArray.add(blue);
        testPlayerColorArray.add(green);
        testPlayerColorArray.add(purple);
        testPlayerColorArray.add(purple);
        testPlayerColorArray.add(green);
    }

    /**
     * Initializes the playerBoard before each test
     */
    @Before
    public void setUp() {
        playerBoard = new PlayerBoard(testColor);
    }

    /**
     * Tests the constructor of the {@link PlayerBoard} class
     * @see PlayerBoard#PlayerBoard(PlayerColor)
     */
    @Test
    public void testConstructor() {
        assertNotNull(playerBoard);
    }

    /**
     * Tests the impossibility that a player could infer a damage to himself
     */
    @Test (expected = IllegalArgumentException.class)
    public void testAppendDamagesColorException() {
        playerBoard.appendDamage(testColor, 3);
    }

    /**
     * Tests the impossibility that a player could infer a mark to himself
     */
    @Test (expected = IllegalArgumentException.class)
    public void testAppendDamagesCountException() {
        playerBoard.appendDamage(green, -1);
    }

    /**
     * Tests how damages are handled
     * @see PlayerBoard#appendDamage(PlayerColor, Integer)
     */
    @Test
    public void testAppendDamages() {
        playerBoard.appendDamage(green, 1);
        playerBoard.appendDamage(blue, 2);
        playerBoard.appendDamage(green, 1);
        playerBoard.appendDamage(purple, 2);
        playerBoard.appendDamage(green, 1);

        assertTrue(playerBoard.getDamage().equals(testPlayerColorArray));
    }

    /**
     * Tests how marks are handled
     * @see PlayerBoard#setMarks(ArrayList)
     */
    @Test
    public void testSetMarks() {
        ArrayList<PlayerColor> toAdd = playerBoard.getMarks();
        toAdd.add(testColor);
        toAdd.add(green);
        toAdd.add(blue);
        try {
            playerBoard.setMarks(toAdd);
        } catch (IllegalArgumentException e) {}
    }

    /**
     * Tests how max points are handled
     * @see PlayerBoard#getMaxPoints()
     */
    @Test
    public void testMaxPointsLogic() {
        assertEquals(playerBoard.getMaxPoints(), (Integer) 8);
        playerBoard.death();
        assertEquals(playerBoard.getMaxPoints(), (Integer) 6);
        playerBoard.death();
        assertEquals(playerBoard.getMaxPoints(), (Integer) 4);
        playerBoard.death();
        assertEquals(playerBoard.getMaxPoints(), (Integer) 2);
        playerBoard.death();
        assertEquals(playerBoard.getMaxPoints(), (Integer) 1);
        playerBoard.death();
        assertEquals(playerBoard.getMaxPoints(), (Integer) 1);
    }

    /**
     * Tests that each variable is cleared after a player's death
     */
    @Test
    public void testDeath() {
        playerBoard.appendDamage(blue, 10);
        playerBoard.setMarks(testPlayerColorArray);
        playerBoard.death();
        assertEquals(playerBoard.getDamage(), emptyPlayerColorArray);
        assertEquals(playerBoard.getMarks(), emptyPlayerColorArray);
    }

    /**
     * Tests that adrenaline phases are correctly calculated
     */
    @Test
    public void testAdrenaline() {
        assertFalse(playerBoard.isAdrenalinic1());
        assertFalse(playerBoard.isAdrenalinic2());

        playerBoard.appendDamage(blue, 3);
        assertTrue(playerBoard.isAdrenalinic1());
        assertFalse(playerBoard.isAdrenalinic2());

        playerBoard.appendDamage(green, 3);
        assertTrue(playerBoard.isAdrenalinic1());
        assertTrue(playerBoard.isAdrenalinic2());
    }

}