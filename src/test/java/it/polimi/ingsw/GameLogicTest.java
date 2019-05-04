package it.polimi.ingsw;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Test for {@link Weapon} class
 *
 * @author Daniele Chiappalupi
 */
public class GameLogicTest {

    private static GameLogic gameLogicTest;
    private static Player p1;
    private static Player p2;
    private static Player p3;
    private static Player p4;
    private static ArrayList<Player> p;
    private static DecksHandler decks;
    private static GameMap map;
    private static LinkedHashMap<Player, Cell> playersPosition;
    private static Board board;


    /**
     * Initializes the attributes for the test class
     */
    @BeforeClass
    public static void setUpBeforeClass() {
        p1 = new Player(new User("username1"), new Character("name1", PlayerColor.green, "description1"));
        p2 = new Player(new User("username2"), new Character("name2", PlayerColor.blue, "description2"));
        p3 = new Player(new User("username3"), new Character("name3", PlayerColor.yellow, "description3"));
        p4 = new Player(new User("username4"), new Character("name4", PlayerColor.grey, "description4"));
        playersPosition = new LinkedHashMap<>();
        playersPosition.put(p1, null);
        playersPosition.put(p2, null);
        playersPosition.put(p3, null);
        playersPosition.put(p4, null);
        map = new GameMap(MapType.conf_4, playersPosition);
        p = new ArrayList<>();
        board = new Board(map, new HashMap<>(), new LinkedHashMap<>());
    }

    /**
     * Initializes the some attributes before each test
     */
    @Before
    public void setUp() {
        gameLogicTest = new GameLogic();
        p.clear();
        p.add(p1);
        p.add(p2);
        p.add(p3);
        p.add(p4);
        decks = new DecksHandler(new ArrayList<>(), new ArrayList<>());
        gameLogicTest.setBoard(board);
        gameLogicTest.setPlayers(p);
    }

    /**
     * Tests the generateTargetsCombination method in some real cases
     * @see GameLogic#generateTargetsCombinations(Effect, ArrayList)
     */
    @Test
    public void testGenerateTargetsCombination() {
        ArrayList<Player> p = new ArrayList<>();
        Player p1 = new Player(new User("username1"), new Character("name1", PlayerColor.green, "description1"));
        Player p2 = new Player(new User("username2"), new Character("name2", PlayerColor.green, "description2"));
        Player p3 = new Player(new User("username3"), new Character("name3", PlayerColor.green, "description3"));
        Player p4 = new Player(new User("username4"), new Character("name4", PlayerColor.green, "description4"));
        p.add(p1);
        p.add(p2);
        p.add(p3);
        p.add(p4);
        DecksHandler decks = new DecksHandler(new ArrayList<AmmoTile>(), new ArrayList<Powerup>());
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("Lock Rifle"))) w = decks.drawWeapon();
        ArrayList<ArrayList<Player>> tester = new ArrayList<>();
        for (int i = 0; i < 4; i++) tester.add(i, new ArrayList<>());
        tester.get(0).add(p1);
        tester.get(1).add(p2);
        tester.get(2).add(p3);
        tester.get(3).add(p4);
        assertEquals(tester, gameLogicTest.generateTargetsCombinations(w.getEffects().get(0), p));

        decks = new DecksHandler(new ArrayList<AmmoTile>(), new ArrayList<Powerup>());
        w = decks.drawWeapon();
        while(!(w.getName().equals("Machine Gun"))) w = decks.drawWeapon();
        tester = new ArrayList<>();
        for (int i = 0; i < 6; i++) tester.add(i, new ArrayList<>());
        tester.get(0).add(p1);
        tester.get(0).add(p2);
        tester.get(1).add(p1);
        tester.get(1).add(p3);
        tester.get(2).add(p1);
        tester.get(2).add(p4);
        tester.get(3).add(p2);
        tester.get(3).add(p3);
        tester.get(4).add(p2);
        tester.get(4).add(p4);
        tester.get(5).add(p3);
        tester.get(5).add(p4);
        assertEquals(tester, gameLogicTest.generateTargetsCombinations(w.getEffects().get(0), p));
    }

    /**
     * Tests the numberCombination method
     * @see GameLogic#combinations(int, int)
     */
    @Test
    public void testCombinations() {
        GameLogic g = new GameLogic();
        HashMap<Integer, ArrayList<Integer>> box = g.combinations(4, 2);
        HashMap<Integer, ArrayList<Integer>> tester = new HashMap<>();
        for(int i = 0; i < 6; i++) tester.put(i, new ArrayList<>());
        tester.get(0).add(1);
        tester.get(0).add(2);
        tester.get(1).add(1);
        tester.get(1).add(3);
        tester.get(2).add(1);
        tester.get(2).add(4);
        tester.get(3).add(2);
        tester.get(3).add(3);
        tester.get(4).add(2);
        tester.get(4).add(4);
        tester.get(5).add(3);
        tester.get(5).add(4);
        assertEquals(tester, box);
    }

    /**
     * Tests the usage of the Whisper weapon
     * @see GameLogic#useEffect(Player, Effect, Weapon)
     */
    @Test
    public void testWeaponWhisper() {
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("Whisper"))) w = decks.drawWeapon();
        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 0, 0);
        map.setPlayerPosition(p4, 1, 3);
        ArrayList<ArrayList<Damage>> tester = new ArrayList<>();
        tester.add(new ArrayList<>());
        tester.get(0).add(new Damage());
        tester.get(0).get(0).setDamage(3);
        tester.get(0).get(0).setTarget(p2);
        tester.get(0).get(0).setMarks(1);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 1);
        map.setPlayerPosition(p3, 2, 3);
        map.setPlayerPosition(p4, 1, 2);
        tester.clear();
        for(int i = 0; i < 3; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(3);
            tester.get(i).get(0).setMarks(1);
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(1).get(0).setTarget(p3);
        tester.get(2).get(0).setTarget(p4);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w));
    }

    /**
     * Tests the usage of the Heatseeker weapon
     * @see GameLogic#useEffect(Player, Effect, Weapon)
     */
    @Test
    public void testWeaponHeatseeker() {
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("Heatseeker"))) w = decks.drawWeapon();
        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 1, 1);
        map.setPlayerPosition(p3, 2, 3);
        map.setPlayerPosition(p4, 1, 2);
        ArrayList<ArrayList<Damage>> tester = new ArrayList<>();
        tester.add(new ArrayList<>());
        tester.get(0).add(new Damage());
        tester.get(0).get(0).setDamage(3);
        tester.get(0).get(0).setTarget(p2);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 1, 1);
        map.setPlayerPosition(p3, 2, 0);
        map.setPlayerPosition(p4, 1, 2);
        tester.clear();
        for(int i = 0; i < 2; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(3);
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(1).get(0).setTarget(p3);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w));
    }

    /**
     * Tests the usage of the Electroscythe weapon, in both the modalities
     * @see GameLogic#useEffect(Player, Effect, Weapon)
     */
    @Test
    public void testWeaponElectroscythe() {
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("Electroscythe"))) w = decks.drawWeapon();
        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 1, 1);
        map.setPlayerPosition(p3, 0, 3);
        map.setPlayerPosition(p4, 1, 2);
        ArrayList<ArrayList<Damage>> tester = new ArrayList<>();
        tester.add(new ArrayList<>());
        tester.get(0).add(new Damage());
        tester.get(0).get(0).setDamage(1);
        tester.get(0).get(0).setTarget(p3);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 3);
        map.setPlayerPosition(p3, 0, 3);
        map.setPlayerPosition(p4, 1, 2);
        tester.clear();
        tester.add(new ArrayList<>());
        for(int i = 0; i < 2; i++) {
            tester.get(0).add(new Damage());
            tester.get(0).get(i).setDamage(1);
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(0).get(1).setTarget(p3);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 1, 1);
        map.setPlayerPosition(p3, 0, 3);
        map.setPlayerPosition(p4, 1, 2);
        tester.clear();
        tester.add(new ArrayList<>());
        tester.get(0).add(new Damage());
        tester.get(0).get(0).setDamage(2);
        tester.get(0).get(0).setTarget(p3);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 3);
        map.setPlayerPosition(p3, 0, 3);
        map.setPlayerPosition(p4, 1, 2);
        tester.clear();
        tester.add(new ArrayList<>());
        for(int i = 0; i < 2; i++) {
            tester.get(0).add(new Damage());
            tester.get(0).get(i).setDamage(2);
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(0).get(1).setTarget(p3);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w));
    }

    /**
     * Tests the Compute Movements method
     * @see GameLogic#computeMovement(Effect, Player, Player)
     */
    @Test
    public void testComputeMovements() {
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("Tractor Beam"))) w = decks.drawWeapon();
        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 0, 0);
        map.setPlayerPosition(p4, 0, 0);
        /*ArrayList<ArrayList<Damage>> tester = new ArrayList<>();
        tester.add(new ArrayList<>());
        tester.get(0).add(new Damage());
        tester.get(0).get(0).setDamage(3);
        tester.get(0).get(0).setTarget(p2);
        tester.get(0).get(0).setMarks(1);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w));*/
        ArrayList<Cell> movements = gameLogicTest.computeMovement(w.getEffects().get(0), p1, p2);
        System.out.println("Cell [1, 2] = " + map.getCell(1,2));
        System.out.println("Cell [1, 3] = " + map.getCell(1,3));
        System.out.println("Cell [2, 2] = " + map.getCell(2,2));
        System.out.println("\n" + movements);
    }
}
