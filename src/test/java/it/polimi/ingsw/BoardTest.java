package it.polimi.ingsw;

import org.junit.*;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Random;

/**
 * Test for {@link Cell} class
 *
 * @author Elena Iannucci
 */

public class BoardTest {

    private Board boardTest;
    private static HashMap<AmmoColor, ArrayList<Weapon>> weaponsdecks = new HashMap<>();
    private static HashMap<Integer, ArrayList<PlayerColor>> skullsTrack = new HashMap<>();
    private static GameMap gameMap;
    private static HashMap<Player, Cell> playersPosition = new HashMap<>();
    private static DecksHandler decksHandler;
    private static ArrayList<Weapon> weapons = new ArrayList<>();
    private static ArrayList<AmmoTile> ammoTiles  = new ArrayList<>();
    private static ArrayList<Powerup> powerups = new ArrayList<>();
    private static Player playerTest;
    private static User userTest;
    private static Character characterTest;
    private static final String username = "Andrea";
    private static Player playerTest2;
    private static User userTest2;
    private static Character characterTest2;
    private static final String username2 = "Barbara";


    @BeforeClass
    public static void setUpBeforeClass() {

        Random rand = new Random();
        int size = 20;
        for (int i=0; i < size; i++) {
            WeaponType type = WeaponType.values()[new Random().nextInt(3)];
            weapons.add(new Weapon("TestWeapon"+i, new ArrayList<>(), "TestNotes"+i, type, new ArrayList<>()));
        }

        size = rand.nextInt(20) + 1;
        for (int i=0; i < size; i++) {
            ArrayList<AmmoColor> colors = new ArrayList<>();
            int costSize = new Random().nextInt(3);
            for(int j=0; j < costSize; j++) colors.add(AmmoColor.values()[new Random().nextInt(3)]);
            ammoTiles.add(new AmmoTile(colors, new Random().nextBoolean()));
        }

        size = rand.nextInt(20) + 1;
        for (int i=0; i < size; i++) {
            powerups.add(new Powerup(PowerupType.values()[new Random().nextInt(4)], "TestDescription"+i, AmmoColor.values()[new Random().nextInt(3)]));
        }

        decksHandler = new DecksHandler((ArrayList<Weapon>)weapons.clone(), (ArrayList<AmmoTile>)ammoTiles.clone(), (ArrayList<Powerup>)powerups.clone());

        userTest = new User(username);
        characterTest = new Character("CharacterTestName", PlayerColor.yellow, "Character user for tests.");
        playerTest = new Player(userTest, characterTest);

        userTest2 = new User(username2);
        characterTest2 = new Character("CharacterTestName2", PlayerColor.blue, "Character user for tests.");
        playerTest2 = new Player(userTest2, characterTest2);

        ArrayList<Weapon> deck1 = new ArrayList<>();
        for(int i=0; i<3; i++){
            deck1.add(decksHandler.drawWeapon());
        }
        weaponsdecks.put(AmmoColor.blue, deck1);

        ArrayList<Weapon> deck2 = new ArrayList<>();
        for(int i=0; i<3; i++){
            deck2.add(decksHandler.drawWeapon());
        }
        weaponsdecks.put(AmmoColor.red, deck2);

        ArrayList<Weapon> deck3 = new ArrayList<>();
        for(int i=0; i<3; i++){
            deck3.add(decksHandler.drawWeapon());
        }
        weaponsdecks.put(AmmoColor.yellow, deck3);

        for (int i=0; i<8; i++){
            skullsTrack.put(i, null);
        }

        playersPosition.put(playerTest, null);
        playersPosition.put(playerTest2, null);

        gameMap = new GameMap(MapType.conf_3, playersPosition);
    }

    @Before
    public void setUp() {
        boardTest = new Board(gameMap, weaponsdecks, skullsTrack);
    }


}
