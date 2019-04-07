package it.polimi.ingsw;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test for {@link Player} class
 *
 * @author Stefano Formicola
 */
public class PlayerTest {

    private Player playerTest;
    private static User userTest;
    private static Character characterTest;
    private static final String username = "Mimmo";
    private static final Integer pointsToAdd = 10;

    /**
     * Initializes the needed attributes for the test class
     */
    @BeforeClass
    public static void setUpClass() {
        userTest = new User(username);
        characterTest = new Character("CharacterTestName", PlayerColor.yellow, "Character user for tests.");
    }

    /**
     * Initializes the player before each test
     */
    @Before
    public void setUp() {
        playerTest = new Player(userTest, characterTest);
    }

    /**
     * Tests the constructor of the {@link Player} class
     * @see Player#Player(User, Character)
     */
    @Test
    public void testConstructor() {
        assertNotNull(playerTest);
    }

    /**
     * Simple getter test
     */
    @Test
    public void getPlayerBoard() {
        assertNotNull(playerTest.getPlayerBoard());
    }

    /**
     * Simple getter test
     */
    @Test
    public void getPlayerHand() {
        assertNotNull(playerTest.getPlayerHand());
    }

    /**
     * Simple getter test
     */
    @Test
    public void getCharacter() {
        assertNotNull(playerTest.getCharacter());
    }

    /**
     * Simple getter test
     */
    @Test
    public void getNickname() {
        assertEquals(username, playerTest.getNickname());
    }

    /**
     * Test the initial value of Player's points
     */
    @Test
    public void getPoints() {
        assertEquals((Integer) 0, playerTest.getPoints());
    }

    /**
     * Tests how points are added through
     * @see Player#addPoints(Integer)
     */
    @Test
    public void addPoints() {
        assertEquals((Integer)0, playerTest.getPoints());

        playerTest.addPoints(pointsToAdd);

        assertEquals(pointsToAdd, playerTest.getPoints());
    }

    /**
     * Tests an exception when a negative value is passed to the
     * @see Player#addPoints(Integer) method
     */
    @Test (expected = IllegalArgumentException.class)
    public void addPointsWithNegativeArgument() {
        playerTest.addPoints(-10);
    }
}