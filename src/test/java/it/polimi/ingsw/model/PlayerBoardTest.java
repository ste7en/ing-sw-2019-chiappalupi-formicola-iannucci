package it.polimi.ingsw.model;

import it.polimi.ingsw.model.utility.PlayerColor;
import it.polimi.ingsw.model.player.PlayerBoard;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

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

        assertEquals(playerBoard.getDamage(), testPlayerColorArray);
    }

    /**
     * Tests how marks are handled
     * @see PlayerBoard#setMarks(List)
     */
    @Test
    public void testSetMarks() {
        List<PlayerColor> toAdd = playerBoard.getMarks();
        toAdd.add(PlayerColor.green);
        toAdd.add(PlayerColor.blue);
        try {
            playerBoard.setMarks(toAdd);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        assertTrue(playerBoard.getMarks().containsAll(toAdd));
    }

    /**
     * Tests how max points are handled
     * @see PlayerBoard#getMaxPoints()
     */
    @Test
    public void testMaxPointsLogic() {
        List<Integer> box = new ArrayList<>();
        box.add(8);
        box.add(6);
        box.add(4);
        box.add(2);
        box.add(1);
        box.add(1);
        for(int i = 0; i < box.size(); i++)
            assertEquals(playerBoard.getMaxPoints().get(i), box.get(i));
    }

    /**
     * Tests how max points are handled
     * @see PlayerBoard#getMaxPoints()
     */
    @Test
    public void testMaxPointsAdrenalinic() {
        playerBoard.turnToFinalFrenzy(true);
        List<Integer> box = new ArrayList<>();
        box.add(2);
        box.add(1);
        box.add(1);
        box.add(1);
        for(int i = 0; i < box.size(); i++)
            assertEquals(playerBoard.getMaxPoints().get(i), box.get(i));
        assertEquals(3, playerBoard.getStepsBeforeGrabbing());
        assertEquals(2, playerBoard.getStepsBeforeShooting());
        assertEquals(0, playerBoard.getStepsOfMovement());
        playerBoard.turnToFinalFrenzy(false);
        for(int i = 0; i < box.size(); i++)
            assertEquals(playerBoard.getMaxPoints().get(i), box.get(i));
        assertEquals(2, playerBoard.getStepsBeforeGrabbing());
        assertEquals(1, playerBoard.getStepsBeforeShooting());
        assertEquals(4, playerBoard.getStepsOfMovement());
    }

    /**
     * Tests that each variable is cleared after a player's death
     */
    @Test
    public void testDeath() {
        playerBoard.appendDamage(PlayerColor.blue, 10);
        playerBoard.setMarks(testPlayerColorArray);
        List<PlayerColor> box = playerBoard.getMarks();
        playerBoard.death(false);
        assertEquals(playerBoard.getDamage(), emptyPlayerColorArray);
        assertEquals(playerBoard.getMarks(), box);
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