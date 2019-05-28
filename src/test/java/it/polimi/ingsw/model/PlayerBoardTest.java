package it.polimi.ingsw.model;

import it.polimi.ingsw.model.utility.PlayerColor;
import it.polimi.ingsw.model.player.PlayerBoard;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.ArrayList;

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
        testColor = PlayerColor.yellow;
        emptyPlayerColorArray = new ArrayList<>();

        testPlayerColorArray = new ArrayList<>();
        testPlayerColorArray.add(PlayerColor.green);
        testPlayerColorArray.add(PlayerColor.blue);
        testPlayerColorArray.add(PlayerColor.blue);
        testPlayerColorArray.add(PlayerColor.green);
        testPlayerColorArray.add(PlayerColor.purple);
        testPlayerColorArray.add(PlayerColor.purple);
        testPlayerColorArray.add(PlayerColor.green);
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
        playerBoard.appendDamage(PlayerColor.green, -1);
    }

    /**
     * Tests how damages are handled
     * @see PlayerBoard#appendDamage(PlayerColor, Integer)
     */
    @Test
    public void testAppendDamages() {
        playerBoard.appendDamage(PlayerColor.green, 1);
        playerBoard.appendDamage(PlayerColor.blue, 2);
        playerBoard.appendDamage(PlayerColor.green, 1);
        playerBoard.appendDamage(PlayerColor.purple, 2);
        playerBoard.appendDamage(PlayerColor.green, 1);

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
        toAdd.add(PlayerColor.green);
        toAdd.add(PlayerColor.blue);
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
        playerBoard.appendDamage(PlayerColor.blue, 10);
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

        playerBoard.appendDamage(PlayerColor.blue, 3);
        assertTrue(playerBoard.isAdrenalinic1());
        assertFalse(playerBoard.isAdrenalinic2());

        playerBoard.appendDamage(PlayerColor.green, 3);
        assertTrue(playerBoard.isAdrenalinic1());
        assertTrue(playerBoard.isAdrenalinic2());
    }

}