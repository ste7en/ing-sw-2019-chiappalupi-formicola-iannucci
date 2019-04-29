package it.polimi.ingsw;

import org.junit.*;
import static org.junit.Assert.*;
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
     * Tests the getCellFromPlayer method
     * @see GameMap#getCellFromPlayer(Player)
     */
    @Test
    public void testGetCellFromPlayer (){
        gameMapTest.setPlayerPosition(playerTest,1,2);
        assertEquals(gameMapTest.getCell(1,2), gameMapTest.getCellFromPlayer(playerTest));
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
     * Tests the getTargetsAtMaxDistance method
     * @see GameMap#getTargetsAtMaxDistance(Cell, int)
     */
    @Test
    public void testGetTargetsAtMaxDistance (){
        ArrayList<Player> targets = new ArrayList<>();
        gameMapTest.setPlayerPosition(playerTest,0,0);
        gameMapTest.setPlayerPosition(playerTest2,0,0);
        targets.add(playerTest);
        targets.add(playerTest2);
        assertEquals(targets, gameMapTest.getTargetsAtMaxDistance(gameMapTest.getCellFromPlayer(playerTest), 0));

        gameMapTest.setPlayerPosition(playerTest,1,0);
        gameMapTest.setPlayerPosition(playerTest2,0,0);
        assertEquals(targets, gameMapTest.getTargetsAtMaxDistance(gameMapTest.getCellFromPlayer(playerTest), 1));

        gameMapTest.setPlayerPosition(playerTest,2,0);
        gameMapTest.setPlayerPosition(playerTest2,1,1);
        assertEquals(targets, gameMapTest.getTargetsAtMaxDistance(gameMapTest.getCellFromPlayer(playerTest), 3));

        gameMapTest.setPlayerPosition(playerTest,2,0);
        gameMapTest.setPlayerPosition(playerTest2,1,1);
        targets.clear();
        targets.add(playerTest);
        assertEquals(targets, gameMapTest.getTargetsAtMaxDistance(gameMapTest.getCellFromPlayer(playerTest), 1));

        gameMapTest.setPlayerPosition(playerTest,2,2);
        gameMapTest.setPlayerPosition(playerTest2,1,1);
        assertEquals(targets, gameMapTest.getTargetsAtMaxDistance(gameMapTest.getCellFromPlayer(playerTest), 1));
    }

    /**
     * Tests the getTargetsAtMinDistance method
     * @see GameMap#getTargetsAtMinDistance(Player, int)
     */
    @Test
    public void testGetTargetsAtMinDistance (){
        ArrayList<Player> targets = new ArrayList<>();
        gameMapTest.setPlayerPosition(playerTest,2,2);
        gameMapTest.setPlayerPosition(playerTest2,1,1);
        targets.add(playerTest2);
        assertEquals(targets, gameMapTest.getTargetsAtMinDistance(playerTest, 1));

        gameMapTest.setPlayerPosition(playerTest,2,0);
        gameMapTest.setPlayerPosition(playerTest2,1,1);
        targets.clear();
        assertEquals(targets, gameMapTest.getTargetsAtMinDistance(playerTest, 3));
    }

    /**
     * Tests the getAdiancentTargets method
     * @see GameMap#getAdiacentTargets(Cell)
     */
    @Test
    public void testGetAdiacentTargets (){
        ArrayList<Player> targets = new ArrayList<>();
        gameMapTest.setPlayerPosition(playerTest,1,0);
        gameMapTest.setPlayerPosition(playerTest2,0,0);
        targets.add(playerTest2);
        assertEquals(targets, gameMapTest.getAdiacentTargets(gameMapTest.getCellFromPlayer(playerTest)));
    }

    /**
     * Tests the getSeenTargets method
     * @see GameMap#getSeenTargets(Player)
     */
    @Test
    public void testGetSeenTargets (){
        ArrayList<Player> targets = new ArrayList<>();
        gameMapTest.setPlayerPosition(playerTest,2,0);
        gameMapTest.setPlayerPosition(playerTest2,0,0);
        targets.add(playerTest);
        targets.add(playerTest2);
        assertEquals(targets, gameMapTest.getSeenTargets(playerTest));

        gameMapTest.setPlayerPosition(playerTest2, 2,1);
        assertEquals(targets, gameMapTest.getSeenTargets(playerTest));

        targets.clear();
        targets.add(playerTest);

        gameMapTest.setPlayerPosition(playerTest2,1,1 );
        assertEquals(targets, gameMapTest.getSeenTargets(playerTest));
    }

    /**
     * Tests the getTargetsFromDirection method
     * @see GameMap#getTargetsFromDirection(Player, Direction)
     */
    @Test
    public void testGetTargetsFromDirection (){
        ArrayList<Player> targets = new ArrayList<>();
        gameMapTest.setPlayerPosition(playerTest,0,0);
        gameMapTest.setPlayerPosition(playerTest2,0,2);
        targets.add(playerTest);
        targets.add(playerTest2);
        assertEquals(targets, gameMapTest.getTargetsFromDirection(playerTest, Direction.East));

        targets.clear();
        targets.add(playerTest);
        assertEquals(targets, gameMapTest.getTargetsFromDirection(playerTest, Direction.North));
        assertEquals(targets, gameMapTest.getTargetsFromDirection(playerTest, Direction.South));
        assertEquals(targets, gameMapTest.getTargetsFromDirection(playerTest, Direction.West));
    }

    /**
     * Tests the isAOneStepValidMove method
     * @see GameMap#isAOneStepValidMove(Player, Cell)
     */
    @Test
    public void testIsOneStepValidMood (){
        gameMapTest.setPlayerPosition(playerTest,1,2);
        assertTrue(gameMapTest.isAOneStepValidMove(playerTest, gameMapTest.getCell(1,3)));
        assertFalse(gameMapTest.isAOneStepValidMove(playerTest, gameMapTest.getCell(1,1)));
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
