package it.polimi.ingsw;

import org.junit.Before;
import org.junit.BeforeClass;

import java.util.HashMap;

/**
 * Test for {@link GameMap} class
 *
 * @author Elena Iannucci
 */

public class GameMapTest {
    private GameMap gameMapTest;
    private static HashMap<Player, Cell> playersPosition = new HashMap<>();
    private static Player playerTest;
    private static User userTest;
    private static Character characterTest;
    private static final String username = "Andrea";
    private static Player playerTest2;
    private static User userTest2;
    private static Character characterTest2;
    private static final String username2 = "Barbara";

    /**
     * Initializes the attributes for the test class
     */
    @BeforeClass
    public static void setUpBeforeClass() {
        userTest = new User(username);
        characterTest = new Character("CharacterTestName", PlayerColor.yellow, "Character user for tests.");
        playerTest = new Player(userTest, characterTest);
        userTest2 = new User(username2);
        characterTest2 = new Character("CharacterTestName2", PlayerColor.blue, "Character user for tests.");
        playerTest2 = new Player(userTest2, characterTest2);
        playersPosition.put(playerTest, null);
        playersPosition.put(playerTest2, null);
    }

    /**
     * Initializes the GameMap before each test
     */
    @Before
    public void setUp() {
        gameMapTest = new GameMap(MapType.conf_3, playersPosition);
    }

}
