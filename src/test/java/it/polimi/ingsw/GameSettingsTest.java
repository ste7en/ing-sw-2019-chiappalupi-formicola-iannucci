package it.polimi.ingsw;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.UUID;

/**
 * Test for {@link GameSettings} class
 *
 * @author Elena Iannucci
 */

public class GameSettingsTest {
    private GameSettings gameSettingsTest;
    private static Player playerTest;
    private static User userTest;
    private static Character characterTest;
    private static final String username = "Andrea";
    private static UUID gameID;

    /**
     * Initializes the attributes for the test class
     */
    @BeforeClass
    public static void setUpBeforeClass() {
        userTest = new User(username);
        characterTest = new Character("CharacterTestName", PlayerColor.yellow, "Character user for tests.");
        playerTest = new Player(userTest, characterTest);
        gameID = UUID.randomUUID();

    }

    /**
     * Initializes the GameSettings before each test
     */
    @Before
    public void setUp() {
        gameSettingsTest = new GameSettings(gameID);
    }

    /**
     * Tests the setFirstPlayer and getFirstPlayer methods
     * @see GameSettings#setFirstPlayer(Player)
     * @see GameSettings#getFirstPlayer()
     */
    @Test
    public void testSetAndGetFirstPlayer(){
        gameSettingsTest.setFirstPlayer(playerTest);
        assertEquals(playerTest, gameSettingsTest.getFirstPlayer());
    }

    /**
     * Tests the setMapType and getMapType methods
     * @see GameSettings#setMapType(MapType)
     * @see GameSettings#getMapType()
     */
    @Test
    public void testSetAndGetMapType(){
        gameSettingsTest.setMapType(MapType.conf_4);
        assertEquals(MapType.conf_4, gameSettingsTest.getMapType());
    }
}
