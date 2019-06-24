package it.polimi.ingsw.model;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Cell;
import it.polimi.ingsw.model.board.GameMap;
import it.polimi.ingsw.model.cards.Weapon;
import it.polimi.ingsw.model.player.Character;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.User;
import it.polimi.ingsw.model.utility.AmmoColor;
import it.polimi.ingsw.model.utility.MapType;
import it.polimi.ingsw.model.utility.PlayerColor;
import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;
import it.polimi.ingsw.controller.*;

/**
 * Test for {@link Board} class
 *
 * @author Elena Iannucci
 */

public class BoardTest {

    private Board boardTest;
    private static GameMap gameMap;
    private static LinkedHashMap<Player, Cell> playersPosition = new LinkedHashMap<>();
    private static Map<AmmoColor, List<Weapon>> weaponsDecks = new HashMap<>();
    private static ArrayList<Weapon> deck1 = new ArrayList<>();
    private static ArrayList<Weapon> deck2 = new ArrayList<>();
    private static ArrayList<Weapon> deck3 = new ArrayList<>();
    private static LinkedHashMap<Integer, ArrayList<PlayerColor>> skullsTrack = new LinkedHashMap<>();
    private static DecksHandler decksHandler;
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
        decksHandler = new DecksHandler();

        userTest = new User(username);
        characterTest = new Character("CharacterTestName", PlayerColor.yellow, "Character user for tests.");
        playerTest = new Player(userTest, characterTest);
        userTest2 = new User(username2);
        characterTest2 = new Character("CharacterTestName2", PlayerColor.blue, "Character user for tests.");
        playerTest2 = new Player(userTest2, characterTest2);
        playersPosition.put(playerTest, null);
        playersPosition.put(playerTest2, null);

        gameMap = new GameMap(MapType.conf_4, playersPosition);

        for(int i=0; i<3; i++){
            deck1.add(decksHandler.drawWeapon());
        }
        weaponsDecks.put(AmmoColor.blue, deck1);

        for(int i=0; i<3; i++){
            deck2.add(decksHandler.drawWeapon());
        }
        weaponsDecks.put(AmmoColor.red, deck2);

        for(int i=0; i<3; i++){
            deck3.add(decksHandler.drawWeapon());
        }
        weaponsDecks.put(AmmoColor.yellow, deck3);
    }

    /**
     * Initializes the Board before each test
     */
    @Before
    public void setUp() {
        boardTest = new Board(gameMap, weaponsDecks, 8);
    }

    /**
     * Tests the pickWeapon method
     * @see Board#pickWeapon(Weapon)
     */
    @Test
    public void testPickWeapon(){
        boardTest.pickWeapon(deck1.get(0));
        assertNull(boardTest.showWeapons(AmmoColor.blue).get(0));
        for (int i=1; i<3; i++) {
            assertNotNull(boardTest.showWeapons(AmmoColor.blue).get(i));
        }
        for (int i=0; i<3; i++) {
            assertNotNull(boardTest.showWeapons(AmmoColor.yellow).get(i));
        }
        for (int i=0; i<3; i++) {
            assertNotNull(boardTest.showWeapons(AmmoColor.red).get(i));
        }
    }

    /**
     * Tests the refillWeapons method
     * @see Board#refillWeapons(DecksHandler)
     */
    @Test
    public void testRefillWeapons(){
        boardTest.pickWeapon(deck1.get(0));
        boardTest.refillWeapons(decksHandler);
        for (int i=0; i<3; i++) {
            assertNotNull(boardTest.showWeapons(AmmoColor.blue).get(i));
        }
        for (int i=0; i<3; i++) {
            assertNotNull(boardTest.showWeapons(AmmoColor.yellow).get(i));
        }
        for (int i=0; i<3; i++) {
            assertNotNull(boardTest.showWeapons(AmmoColor.red).get(i));
        }
    }

    /**
     * Tests the showWeapons method
     * @see Board#showWeapons(AmmoColor)
     */
    @Test
    public void testShowWeapons(){
        assertEquals(deck1, boardTest.showWeapons(AmmoColor.blue));
        assertEquals(deck2, boardTest.showWeapons(AmmoColor.red));
        assertEquals(deck3, boardTest.showWeapons(AmmoColor.yellow));
    }

    /**
     * Tests the skullsLeft method
     * @see Board#skullsLeft()
     */
    @Test
    public void testSkullsLeft(){
        assertEquals(8, boardTest.skullsLeft());
        boardTest.addBloodFrom(playerTest.getCharacter().getColor(), 2);
        assertEquals(7, boardTest.skullsLeft());
        for (int i=1;i<8; i++){
            boardTest.addBloodFrom(playerTest2.getCharacter().getColor(), 1);
        }
        assertEquals(0, boardTest.skullsLeft());
    }

    /**
     * Tests the addBloodFrom and getBlood methods
     * @see Board#addBloodFrom(PlayerColor, Integer)
     * @see Board#getBlood(int)
     */
    @Test
    public void testAddAndGetBloodFrom (){
        ArrayList<PlayerColor> blood = new ArrayList<>();
        ArrayList<PlayerColor> blood2 = new ArrayList<>();
        blood.add(PlayerColor.yellow);
        blood.add(PlayerColor.yellow);
        blood2.add(PlayerColor.blue);
        boardTest.addBloodFrom(playerTest.getCharacter().getColor(), 2);
        boardTest.addBloodFrom(playerTest2.getCharacter().getColor(), 1);
        assertEquals(6, boardTest.skullsLeft());
    }
}
