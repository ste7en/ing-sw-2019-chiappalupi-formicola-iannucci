package it.polimi.ingsw;

import org.junit.*;

import static org.junit.Assert.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Test for {@link GameMap} class
 *
 * @author Elena Iannucci
 */

public class GameMapTest {
    private GameMap gameMapTest;
    private static LinkedHashMap<Player, Cell> playersPosition = new LinkedHashMap<>();
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
        gameMapTest = new GameMap(MapType.conf_4, playersPosition);
    }

    /**
     * Tests the getCell method
     * @see GameMap#getCell(int, int)
     */
    @Test
    public void testGetCell (){
        Cell cellTest = gameMapTest.getCell(0,1);
        assertNotNull(gameMapTest.getRoomFromCell(cellTest));
        assertEquals(cellTest, gameMapTest.getCell(0,1));
    }

    /**
     * Tests the getRoomFromCell method
     * @see GameMap#getRoomFromCell(Cell)
     */
    @Test
    public void testGetRoomFromCell (){
        ArrayList <Cell> blueRoom = new ArrayList<>();
        Cell cellTest = gameMapTest.getCell(0,2);
        blueRoom.add(gameMapTest.getCell(0,1));
        blueRoom.add(gameMapTest.getCell(0,2));
        assertNotNull(gameMapTest.getRoomFromCell(cellTest));
        assertEquals(blueRoom, gameMapTest.getRoomFromCell(cellTest));

        ArrayList <Cell> yellowRoom = new ArrayList<>();
        Cell cellTest2 = gameMapTest.getCell(1,3);
        yellowRoom.add(gameMapTest.getCell(1,2));
        yellowRoom.add(gameMapTest.getCell(1,3));
        yellowRoom.add(gameMapTest.getCell(2,2));
        yellowRoom.add(gameMapTest.getCell(2,3));
        assertNotNull(gameMapTest.getRoomFromCell(cellTest));
        assertEquals(yellowRoom, gameMapTest.getRoomFromCell(cellTest2));

        ArrayList <Cell> pinkRoom = new ArrayList<>();
        Cell cellTest3 = gameMapTest.getCell(1,1);
        pinkRoom.add(gameMapTest.getCell(1,1));
        assertNotNull(gameMapTest.getRoomFromCell(cellTest));
        assertEquals(pinkRoom, gameMapTest.getRoomFromCell(cellTest3));

    }

    /**
     * Tests the getTargetsInMyCell method
     * @see GameMap#getTargetsInMyCell(Player)
     */
    @Test
    public void testGetTargetsInMyCell (){
        ArrayList<Player> playersInThatCell = new ArrayList<>();
        gameMapTest.setPlayerPosition(playerTest,1,2);
        gameMapTest.setPlayerPosition(playerTest2, 1,2);
        playersInThatCell.add(playerTest);
        playersInThatCell.add(playerTest2);
        assertEquals(playersInThatCell, gameMapTest.getTargetsInMyCell(playerTest));
    }

    /**
     * Tests the getOneMoveAwayTargets method
     * @see GameMap#getOneMoveAwayTargets(Player)
     */
    @Test
    public void testGetOneMoveAwayTargets (){
        Player playerTest3;
        User userTest3;
        Character characterTest3;
        String username3 = "Barbara";
        userTest3 = new User(username3);
        characterTest3 = new Character("CharacterTestName3", PlayerColor.green, "Character user for tests.");
        playerTest3 = new Player(userTest3, characterTest3);

        ArrayList<Player> adiacentPlayers = new ArrayList<>();
        gameMapTest.setPlayerPosition(playerTest,1,2);
        gameMapTest.setPlayerPosition(playerTest2, 0,2);
        gameMapTest.setPlayerPosition(playerTest3, 1,3);
        adiacentPlayers.add(playerTest2);
        adiacentPlayers.add(playerTest3);

        assertEquals(adiacentPlayers, gameMapTest.getOneMoveAwayTargets(playerTest));
    }

    /**
     * Tests the getPositionFromPlayer method
     * @see GameMap#getPositionFromPlayer(Player)
     */
    @Test
    public void testGetPositionFromPlayer (){
        gameMapTest.setPlayerPosition(playerTest,1,2);
        Cell positionCell = gameMapTest.getCell(1,2);
        assertEquals(positionCell, gameMapTest.getPositionFromPlayer(playerTest));
    }

}
