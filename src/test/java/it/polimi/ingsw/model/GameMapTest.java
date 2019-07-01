package it.polimi.ingsw.model;

import it.polimi.ingsw.model.board.Cell;
import it.polimi.ingsw.model.board.GameMap;
import it.polimi.ingsw.model.player.Character;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.User;
import it.polimi.ingsw.model.utility.Direction;
import it.polimi.ingsw.model.utility.MapType;
import it.polimi.ingsw.model.utility.PlayerColor;
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
    }

    /**
     * Initializes the GameMap before each test
     */
    @Before
    public void setUp() {
        gameMapTest = new GameMap(MapType.conf_4);
        for(Player p: playersPosition.keySet()) {
            gameMapTest.setPlayerPosition(p, gameMapTest.getCell(1, 2));
        }
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
    public void testGetTargetsInMyCell(){
        ArrayList<Player> playersInThatCell = new ArrayList<>();
        gameMapTest.setPlayerPosition(playerTest,1,2);
        gameMapTest.setPlayerPosition(playerTest2, 1,2);
        playersInThatCell.add(playerTest2);
        assertEquals(playersInThatCell, gameMapTest.getTargetsInMyCell(playerTest));
    }

    /**
     * Tests the getCellsAtMaxDistance method
     * @see GameMap#getCellsAtMaxDistance(Player, int)
     */
    @Test
    public void testGetCellsAtMaxDistance(){
        ArrayList<Cell> cells = new ArrayList<>();
        assertEquals(cells, gameMapTest.getCellsAtMaxDistance(playerTest, 0));

        cells.add(gameMapTest.getCell(0,0));
        cells.add(gameMapTest.getCell(1,0));
        cells.add(gameMapTest.getCell(1,1));
        cells.add(gameMapTest.getCell(2,2));
        cells.add(gameMapTest.getCell(2,1));

        gameMapTest.setPlayerPosition(playerTest, 2,0);

        assertEquals(cells, gameMapTest.getCellsAtMaxDistance(playerTest, 2));
    }

    /**
     * Tests the getTargetsAtMaxDistance method
     * @see GameMap#getTargetsAtMaxDistance(Player, int)
     */
    @Test
    public void testGetTargetsAtMaxDistance (){
        ArrayList<Player> targets = new ArrayList<>();
        gameMapTest.setPlayerPosition(playerTest,0,0);
        gameMapTest.setPlayerPosition(playerTest2,0,0);
        targets.add(playerTest2);
        assertEquals(targets, gameMapTest.getTargetsAtMaxDistance(playerTest, 0));

        gameMapTest.setPlayerPosition(playerTest,1,0);
        gameMapTest.setPlayerPosition(playerTest2,0,0);
        assertEquals(targets, gameMapTest.getTargetsAtMaxDistance(playerTest, 1));

        gameMapTest.setPlayerPosition(playerTest,2,0);
        gameMapTest.setPlayerPosition(playerTest2,1,1);
        assertEquals(targets, gameMapTest.getTargetsAtMaxDistance(playerTest, 3));

        gameMapTest.setPlayerPosition(playerTest,2,0);
        gameMapTest.setPlayerPosition(playerTest2,1,1);
        targets.clear();
        assertEquals(targets, gameMapTest.getTargetsAtMaxDistance(playerTest, 1));

        gameMapTest.setPlayerPosition(playerTest,2,2);
        gameMapTest.setPlayerPosition(playerTest2,1,1);
        assertEquals(targets, gameMapTest.getTargetsAtMaxDistance(playerTest, 1));
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

        gameMapTest.setPlayerPosition(playerTest2,2,2);
        assertEquals(targets, gameMapTest.getTargetsAtMinDistance(playerTest, 0));

        gameMapTest.setPlayerPosition(playerTest,2,0);
        gameMapTest.setPlayerPosition(playerTest2,1,1);
        targets.clear();
        assertEquals(targets, gameMapTest.getTargetsAtMinDistance(playerTest, 3));
    }

    /**
     * Tests the getAdjancentTargets method
     * @see GameMap#getAdjacentTargets(Cell)
     */
    @Test
    public void testGetAdjacentTargets(){
        ArrayList<Player> targets = new ArrayList<>();
        gameMapTest.setPlayerPosition(playerTest,1,0);
        gameMapTest.setPlayerPosition(playerTest2,0,0);
        targets.add(playerTest2);
        assertEquals(targets, gameMapTest.getAdjacentTargets(gameMapTest.getCellFromPlayer(playerTest)));
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
        targets.add(playerTest2);
        assertEquals(targets, gameMapTest.getSeenTargets(playerTest));

        gameMapTest.setPlayerPosition(playerTest2, 2,1);
        assertEquals(targets, gameMapTest.getSeenTargets(playerTest));

        gameMapTest.setPlayerPosition(playerTest2,1,1 );
        targets.clear();
        assertEquals(targets, gameMapTest.getSeenTargets(playerTest));
    }

    /**
     * Tests the getUnseenTargets method
     * @see GameMap#getUnseenTargets(Player)
     */
    @Test
    public void testGetUnseenTargets () {
        ArrayList<Player> targets = new ArrayList<>();
        gameMapTest.setPlayerPosition(playerTest, 2, 3);
        gameMapTest.setPlayerPosition(playerTest2, 1, 1);
        targets.add(playerTest2);
        assertEquals(targets, gameMapTest.getUnseenTargets(playerTest));

        gameMapTest.setPlayerPosition(playerTest2, 1, 2);
        targets.clear();
        assertEquals(targets, gameMapTest.getUnseenTargets(playerTest));
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
        targets.add(playerTest2);
        assertEquals(targets, gameMapTest.getTargetsFromDirection(playerTest, Direction.East));

        targets.clear();
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
     * Tests the setPlayerPosition and getPositionFromPlayer methods
     * @see GameMap#setPlayerPosition(Player, Cell)
     * @see GameMap#setPlayerPosition(Player, int, int)
     * @see GameMap#getPositionFromPlayer(Player)
     */
    @Test
    public void testSetPlayerPositionGetPositionFromPlayer (){
        gameMapTest.setPlayerPosition(playerTest,1,2);
        Cell positionCell = gameMapTest.getCell(1,2);
        assertEquals(positionCell, gameMapTest.getPositionFromPlayer(playerTest));

        gameMapTest.setPlayerPosition(playerTest2, gameMapTest.getCell(1,2));
        assertEquals(positionCell, gameMapTest.getPositionFromPlayer(playerTest));
    }

    /**
     * Tests the toString method, asserting that the string is not null.
     * @see GameMap#toString()
     */
    @Test
    public void testToString() {
        String map = gameMapTest.toString();
        assertNotNull(map);
    }

    /**
     * Tests the deserialization of the map from the json.
     * @see GameMap#GameMap(MapType)
     */
    @Test
    public void testInitializeMapFromJson() {
        GameMap map1 = new GameMap(MapType.conf_1);
        assertNotNull(map1);
        System.out.println(map1.toString());

        GameMap map2 = new GameMap(MapType.conf_2);
        assertNotNull(map2);
        System.out.println(map2.toString());

        GameMap map3 = new GameMap(MapType.conf_3);
        assertNotNull(map3);
        System.out.println(map3.toString());

        GameMap map4 = new GameMap(MapType.conf_4);
        assertNotNull(map4);
        System.out.println(map4.toString());
    }
}
