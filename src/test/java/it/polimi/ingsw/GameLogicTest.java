package it.polimi.ingsw;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.*;

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
        LinkedHashMap<Player, Cell> playersPosition = new LinkedHashMap<>();
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
     *
     * @see GameLogic#generateTargetsCombinations(Effect, ArrayList, Player)
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
        DecksHandler decks = new DecksHandler(new ArrayList<>(), new ArrayList<>());
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("Lock Rifle"))) w = decks.drawWeapon();
        ArrayList<ArrayList<Player>> tester = new ArrayList<>();
        for (int i = 0; i < 4; i++) tester.add(i, new ArrayList<>());
        tester.get(0).add(p1);
        tester.get(1).add(p2);
        tester.get(2).add(p3);
        tester.get(3).add(p4);
        assertEquals(tester, gameLogicTest.generateTargetsCombinations(w.getEffects().get(0), p, p1));

        decks = new DecksHandler(new ArrayList<>(), new ArrayList<>());
        w = decks.drawWeapon();
        while (!(w.getName().equals("Machine Gun"))) w = decks.drawWeapon();
        tester = new ArrayList<>();
        for (int i = 0; i < 10; i++) tester.add(i, new ArrayList<>());
        tester.get(0).add(p1);
        tester.get(1).add(p2);
        tester.get(2).add(p3);
        tester.get(3).add(p4);
        tester.get(4).add(p1);
        tester.get(4).add(p2);
        tester.get(5).add(p1);
        tester.get(5).add(p3);
        tester.get(6).add(p1);
        tester.get(6).add(p4);
        tester.get(7).add(p2);
        tester.get(7).add(p3);
        tester.get(8).add(p2);
        tester.get(8).add(p4);
        tester.get(9).add(p3);
        tester.get(9).add(p4);
        assertEquals(tester, gameLogicTest.generateTargetsCombinations(w.getEffects().get(0), p, p1));
    }

    /**
     * Tests the numberCombination method
     *
     * @see GameLogic#combinationsWithLowerValues(int, int)
     */
    @Test
    public void testCombinationsWithLowerValues() {
        GameLogic g = new GameLogic();
        HashMap<Integer, ArrayList<Integer>> box = g.combinationsWithLowerValues(4, 2);
        HashMap<Integer, ArrayList<Integer>> tester = new HashMap<>();
        for (int i = 0; i < 10; i++) tester.put(i, new ArrayList<>());
        tester.get(0).add(1);
        tester.get(1).add(2);
        tester.get(2).add(3);
        tester.get(3).add(4);
        tester.get(4).add(1);
        tester.get(4).add(2);
        tester.get(5).add(1);
        tester.get(5).add(3);
        tester.get(6).add(1);
        tester.get(6).add(4);
        tester.get(7).add(2);
        tester.get(7).add(3);
        tester.get(8).add(2);
        tester.get(8).add(4);
        tester.get(9).add(3);
        tester.get(9).add(4);
        assertEquals(tester, box);
    }

    /**
     * Tests the usage of the Whisper weapon in different situations
     *
     * @see GameLogic#useEffect(Player, Effect, Weapon, ArrayList)
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
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 1);
        map.setPlayerPosition(p3, 2, 3);
        map.setPlayerPosition(p4, 1, 2);
        tester.clear();
        for (int i = 0; i < 3; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(3);
            tester.get(i).get(0).setMarks(1);
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(1).get(0).setTarget(p3);
        tester.get(2).get(0).setTarget(p4);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 0);
        map.setPlayerPosition(p3, 2, 0);
        map.setPlayerPosition(p4, 1, 1);
        tester.clear();
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));
    }

    /**
     * Tests the usage of the Heatseeker weapon in different situations
     *
     * @see GameLogic#useEffect(Player, Effect, Weapon, ArrayList)
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
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 1, 1);
        map.setPlayerPosition(p3, 2, 0);
        map.setPlayerPosition(p4, 1, 2);
        tester.clear();
        for (int i = 0; i < 2; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(3);
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(1).get(0).setTarget(p3);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));
    }

    /**
     * Tests the usage of the Electroscythe weapon, in both the modalities and in different situations
     *
     * @see GameLogic#useEffect(Player, Effect, Weapon, ArrayList)
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
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 3);
        map.setPlayerPosition(p3, 0, 3);
        map.setPlayerPosition(p4, 1, 2);
        tester.clear();
        tester.add(new ArrayList<>());
        for (int i = 0; i < 2; i++) {
            tester.get(0).add(new Damage());
            tester.get(0).get(i).setDamage(1);
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(0).get(1).setTarget(p3);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 1, 1);
        map.setPlayerPosition(p3, 0, 3);
        map.setPlayerPosition(p4, 1, 2);
        tester.clear();
        tester.add(new ArrayList<>());
        tester.get(0).add(new Damage());
        tester.get(0).get(0).setDamage(2);
        tester.get(0).get(0).setTarget(p3);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 3);
        map.setPlayerPosition(p3, 0, 3);
        map.setPlayerPosition(p4, 1, 2);
        tester.clear();
        tester.add(new ArrayList<>());
        for (int i = 0; i < 2; i++) {
            tester.get(0).add(new Damage());
            tester.get(0).get(i).setDamage(2);
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(0).get(1).setTarget(p3);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));
    }

    /**
     * Tests the Compute Movements method
     *
     * @see GameLogic#computeMovement(Effect, Player, Player, Cell)
     */
    @Test
    public void testComputeMovements() {
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("Tractor Beam"))) w = decks.drawWeapon();
        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 0, 0);
        map.setPlayerPosition(p4, 0, 0);
        ArrayList<Cell> tester = new ArrayList<>();
        tester.add(map.getCell(2, 3));
        tester.add(map.getCell(1, 3));
        tester.add(map.getCell(2, 2));
        tester.add(map.getCell(1, 2));
        tester.sort(Cell::compareTo);
        assertEquals(tester, gameLogicTest.computeMovement(w.getEffects().get(0), p1, p2, null));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 2, 1);
        map.setPlayerPosition(p3, 0, 0);
        map.setPlayerPosition(p4, 0, 0);
        tester.clear();
        tester.add(map.getCell(0, 1));
        tester.add(map.getCell(1, 2));
        tester.add(map.getCell(2, 2));
        tester.add(map.getCell(2, 3));
        tester.sort(Cell::compareTo);
        assertEquals(tester, gameLogicTest.computeMovement(w.getEffects().get(0), p1, p2, null));
    }

    /**
     * Tests the usage of the Tractor Beam weapon, in both the modalities and in different situations
     *
     * @see GameLogic#useEffect(Player, Effect, Weapon, ArrayList)
     */
    @Test
    public void testWeaponTractorBeam() {
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("Tractor Beam"))) w = decks.drawWeapon();

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 1, 2);
        map.setPlayerPosition(p4, 0, 0);
        ArrayList<ArrayList<Damage>> tester = new ArrayList<>();
        ArrayList<Cell> movementsP2 = gameLogicTest.computeMovement(w.getEffects().get(0), p1, p2, null);
        ArrayList<Cell> movementsP3 = gameLogicTest.computeMovement(w.getEffects().get(0), p1, p3, null);
        ArrayList<Cell> movements;
        for (int i = 0; i < 8; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(1);
            tester.get(i).get(0).setTarget((i < 4) ? p2 : p3);
            tester.get(i).get(0).setPosition((i < 4) ? movementsP2.get(i) : movementsP3.get(i - 4));
        }
        ArrayList<Cell> movementsCheck = new ArrayList<>();
        movementsCheck.add(map.getCell(1, 2));
        movementsCheck.add(map.getCell(1, 3));
        movementsCheck.add(map.getCell(2, 2));
        movementsCheck.add(map.getCell(2, 3));
        movementsCheck.sort(Cell::compareTo);
        assertEquals(movementsP2, movementsCheck);
        assertEquals(movementsP3, movementsCheck);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 0, 0);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 2, 3);
        map.setPlayerPosition(p4, 2, 3);
        tester.clear();
        movements = gameLogicTest.computeMovement(w.getEffects().get(0), p1, p2, null);
        movementsCheck.clear();
        for (int i = 0; i < 2; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(1);
            tester.get(i).get(0).setTarget(p2);
            tester.get(i).get(0).setPosition(movements.get(i));
        }
        movementsCheck.add(map.getCell(0, 1));
        movementsCheck.add(map.getCell(0, 2));
        movementsCheck.sort(Cell::compareTo);
        assertEquals(movements, movementsCheck);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 0, 0);
        map.setPlayerPosition(p2, 0, 2);
        map.setPlayerPosition(p3, 2, 3);
        map.setPlayerPosition(p4, 2, 3);
        tester.clear();
        tester.add(new ArrayList<>());
        tester.get(0).add(new Damage());
        tester.get(0).get(0).setDamage(3);
        tester.get(0).get(0).setTarget(p2);
        tester.get(0).get(0).setPosition(map.getCell(0, 0));
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));

        map.setPlayerPosition(p1, 0, 0);
        map.setPlayerPosition(p2, 0, 2);
        map.setPlayerPosition(p3, 0, 1);
        map.setPlayerPosition(p4, 0, 0);
        tester.clear();
        for (int i = 0; i < 3; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(3);
            tester.get(i).get(0).setPosition(map.getCell(0, 0));
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(1).get(0).setTarget(p3);
        tester.get(2).get(0).setTarget(p4);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));
    }

    /**
     * Tests the usage of the Furnace weapon, in both the modalities and in different situations
     *
     * @see GameLogic#useEffect(Player, Effect, Weapon, ArrayList)
     */
    @Test
    public void testWeaponFurnace() {
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("Furnace"))) w = decks.drawWeapon();

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 2, 2);
        map.setPlayerPosition(p3, 1, 3);
        map.setPlayerPosition(p4, 2, 2);
        ArrayList<ArrayList<Damage>> tester = new ArrayList<>();
        tester.add(new ArrayList<>());
        tester.add(new ArrayList<>());
        for (int i = 0; i < 2; i++) {
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(1);
            tester.get(i).get(0).setMarks(1);
        }
        tester.get(0).add(new Damage());
        tester.get(0).get(1).setTarget(p4);
        tester.get(0).get(1).setDamage(1);
        tester.get(0).get(1).setMarks(1);
        tester.get(0).get(0).setTarget(p2);
        tester.get(1).get(0).setTarget(p3);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 0, 2);
        map.setPlayerPosition(p3, 0, 3);
        map.setPlayerPosition(p4, 0, 2);
        tester.clear();
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 2, 2);
        map.setPlayerPosition(p3, 2, 2);
        map.setPlayerPosition(p4, 2, 2);
        tester.clear();
        tester.add(new ArrayList<>());
        for (int i = 0; i < 3; i++) {
            tester.get(0).add(new Damage());
            tester.get(0).get(i).setDamage(1);
            tester.get(0).get(i).setMarks(1);
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(0).get(1).setTarget(p3);
        tester.get(0).get(2).setTarget(p4);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 2, 2);
        map.setPlayerPosition(p3, 0, 0);
        map.setPlayerPosition(p4, 1, 1);
        tester.clear();
        for (int i = 0; i < 2; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(1);
        }
        tester.get(0).get(0).setTarget(p3);
        tester.get(1).get(0).setTarget(p4);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 2, 2);
        map.setPlayerPosition(p3, 1, 2);
        map.setPlayerPosition(p4, 1, 3);
        tester.clear();
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 0, 0);
        map.setPlayerPosition(p3, 0, 0);
        map.setPlayerPosition(p4, 0, 0);
        tester.clear();
        tester.add(new ArrayList<>());
        for (int i = 0; i < 3; i++) {
            tester.get(0).add(new Damage());
            tester.get(0).get(i).setDamage(1);
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(0).get(1).setTarget(p3);
        tester.get(0).get(2).setTarget(p4);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

    }

    /**
     * Tests the usage of the Furnace weapon, in both the modalities and in different situations
     *
     * @see GameLogic#useEffect(Player, Effect, Weapon, ArrayList)
     */
    @Test
    public void testWeaponHellion() {
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("Hellion"))) w = decks.drawWeapon();

        map.setPlayerPosition(p1, 1, 0);
        map.setPlayerPosition(p2, 0, 0);
        map.setPlayerPosition(p3, 1, 1);
        map.setPlayerPosition(p4, 0, 0);
        ArrayList<ArrayList<Damage>> tester = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            tester.add(new ArrayList<>());
            for (int j = 0; j < 2; j++) {
                tester.get(i).add(new Damage());
                if (j == 0) tester.get(i).get(j).setDamage(1);
                tester.get(i).get(j).setMarks(1);
            }
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(0).get(1).setTarget(p4);
        tester.get(1).get(0).setTarget(p4);
        tester.get(1).get(1).setTarget(p2);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 0, 1);
        map.setPlayerPosition(p2, 1, 1);
        map.setPlayerPosition(p3, 1, 1);
        map.setPlayerPosition(p4, 1, 1);
        tester.clear();
        for (int i = 0; i < 3; i++) {
            tester.add(new ArrayList<>());
            for (int j = 0; j < 3; j++) {
                tester.get(i).add(new Damage());
                if (j == 0) tester.get(i).get(j).setDamage(1);
                tester.get(i).get(j).setMarks(1);
            }
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(0).get(1).setTarget(p3);
        tester.get(0).get(2).setTarget(p4);
        tester.get(1).get(0).setTarget(p3);
        tester.get(1).get(1).setTarget(p2);
        tester.get(1).get(2).setTarget(p4);
        tester.get(2).get(0).setTarget(p4);
        tester.get(2).get(1).setTarget(p2);
        tester.get(2).get(2).setTarget(p3);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 0, 1);
        map.setPlayerPosition(p2, 0, 1);
        map.setPlayerPosition(p3, 2, 1);
        map.setPlayerPosition(p4, 2, 3);
        tester.clear();
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 1, 0);
        map.setPlayerPosition(p2, 0, 0);
        map.setPlayerPosition(p3, 1, 1);
        map.setPlayerPosition(p4, 0, 0);
        tester.clear();
        for (int i = 0; i < 2; i++) {
            tester.add(new ArrayList<>());
            for (int j = 0; j < 2; j++) {
                tester.get(i).add(new Damage());
                if (j == 0) tester.get(i).get(j).setDamage(1);
                tester.get(i).get(j).setMarks(2);
            }
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(0).get(1).setTarget(p4);
        tester.get(1).get(0).setTarget(p4);
        tester.get(1).get(1).setTarget(p2);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));

        map.setPlayerPosition(p1, 0, 1);
        map.setPlayerPosition(p2, 1, 1);
        map.setPlayerPosition(p3, 1, 1);
        map.setPlayerPosition(p4, 1, 1);
        tester.clear();
        for (int i = 0; i < 3; i++) {
            tester.add(new ArrayList<>());
            for (int j = 0; j < 3; j++) {
                tester.get(i).add(new Damage());
                if (j == 0) tester.get(i).get(j).setDamage(1);
                tester.get(i).get(j).setMarks(2);
            }
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(0).get(1).setTarget(p3);
        tester.get(0).get(2).setTarget(p4);
        tester.get(1).get(0).setTarget(p3);
        tester.get(1).get(1).setTarget(p2);
        tester.get(1).get(2).setTarget(p4);
        tester.get(2).get(0).setTarget(p4);
        tester.get(2).get(1).setTarget(p2);
        tester.get(2).get(2).setTarget(p3);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));

        map.setPlayerPosition(p1, 0, 1);
        map.setPlayerPosition(p2, 0, 1);
        map.setPlayerPosition(p3, 2, 1);
        map.setPlayerPosition(p4, 2, 3);
        tester.clear();
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));
    }

    /**
     * Tests the usage of the Flamethrower weapon, in both the modalities and in different situations
     *
     * @see GameLogic#useEffect(Player, Effect, Weapon, ArrayList)
     */
    @Test
    public void testWeaponFlamethrower() {
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("Flamethrower"))) w = decks.drawWeapon();

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 1, 3);
        map.setPlayerPosition(p4, 0, 3);
        ArrayList<ArrayList<Damage>> tester = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(1);
        }
        tester.get(0).get(0).setTarget(p3);
        tester.get(1).get(0).setTarget(p4);
        tester.get(2).get(0).setTarget(p3);
        tester.get(2).add(new Damage());
        tester.get(2).get(1).setTarget(p4);
        tester.get(2).get(1).setDamage(1);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 1, 1);
        map.setPlayerPosition(p4, 0, 2);
        tester.clear();
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 2);
        map.setPlayerPosition(p3, 1, 3);
        map.setPlayerPosition(p4, 2, 3);
        tester.clear();
        for (int i = 0; i < 4; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(1);
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(1).get(0).setTarget(p3);
        tester.get(2).get(0).setTarget(p4);
        tester.get(3).get(0).setTarget(p3);
        tester.get(3).add(new Damage());
        tester.get(3).get(1).setTarget(p4);
        tester.get(3).get(1).setDamage(1);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 2);
        map.setPlayerPosition(p3, 0, 3);
        map.setPlayerPosition(p4, 2, 3);
        tester.clear();
        for (int i = 0; i < 2; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(1);
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(1).get(0).setTarget(p4);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 2);
        map.setPlayerPosition(p3, 0, 1);
        map.setPlayerPosition(p4, 2, 3);
        tester.clear();
        for (int i = 0; i < 2; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
        }
        tester.get(1).get(0).setTarget(p2);
        tester.get(1).get(0).setDamage(2);
        tester.get(1).add(new Damage());
        tester.get(1).get(1).setTarget(p3);
        tester.get(1).get(1).setDamage(1);
        tester.get(0).get(0).setTarget(p4);
        tester.get(0).get(0).setDamage(1);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 0);
        map.setPlayerPosition(p3, 1, 1);
        map.setPlayerPosition(p4, 1, 2);
        tester.clear();
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 2);
        map.setPlayerPosition(p3, 0, 1);
        map.setPlayerPosition(p4, 0, 3);
        tester.clear();
        tester.add(new ArrayList<>());
        tester.get(0).add(new Damage());
        tester.get(0).get(0).setTarget(p2);
        tester.get(0).get(0).setDamage(2);
        tester.get(0).add(new Damage());
        tester.get(0).get(1).setTarget(p3);
        tester.get(0).get(1).setDamage(1);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));
    }

    /**
     * Tests the usage of the Railgun weapon, in both the modalities and in different situations
     *
     * @see GameLogic#useEffect(Player, Effect, Weapon, ArrayList)
     */
    @Test
    public void testWeaponRailgun() {
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("Railgun"))) w = decks.drawWeapon();

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 1, 3);
        map.setPlayerPosition(p4, 0, 3);
        ArrayList<ArrayList<Damage>> tester = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(3);
        }
        tester.get(0).get(0).setTarget(p3);
        tester.get(1).get(0).setTarget(p4);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 1, 1);
        map.setPlayerPosition(p4, 0, 2);
        tester.clear();
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 2, 3);
        map.setPlayerPosition(p3, 1, 3);
        map.setPlayerPosition(p4, 0, 3);
        tester.clear();
        for (int i = 0; i < 3; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(3);
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(1).get(0).setTarget(p3);
        tester.get(2).get(0).setTarget(p4);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 1, 3);
        map.setPlayerPosition(p4, 0, 3);
        tester.clear();
        for (int i = 0; i < 3; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(2);
        }
        tester.get(0).get(0).setTarget(p3);
        tester.get(1).get(0).setTarget(p4);
        tester.get(2).get(0).setTarget(p3);
        tester.get(2).add(new Damage());
        tester.get(2).get(1).setTarget(p4);
        tester.get(2).get(1).setDamage(2);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 1, 1);
        map.setPlayerPosition(p4, 0, 2);
        tester.clear();
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 2, 3);
        map.setPlayerPosition(p3, 1, 3);
        map.setPlayerPosition(p4, 0, 3);
        tester.clear();
        for (int i = 0; i < 6; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(2);
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(1).get(0).setTarget(p3);
        tester.get(2).get(0).setTarget(p4);
        tester.get(3).get(0).setTarget(p2);
        tester.get(3).get(0).setTarget(p2);
        tester.get(3).add(new Damage());
        tester.get(3).get(1).setTarget(p3);
        tester.get(3).get(1).setDamage(2);
        tester.get(4).get(0).setTarget(p2);
        tester.get(4).add(new Damage());
        tester.get(4).get(1).setTarget(p4);
        tester.get(4).get(1).setDamage(2);
        tester.get(5).get(0).setTarget(p3);
        tester.get(5).add(new Damage());
        tester.get(5).get(1).setTarget(p4);
        tester.get(5).get(1).setDamage(2);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));
    }

    /**
     * Tests the usage of the ZX-2 weapon, in both the modalities and in different situations
     *
     * @see GameLogic#useEffect(Player, Effect, Weapon, ArrayList)
     */
    @Test
    public void testWeaponZX2() {
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("ZX-2"))) w = decks.drawWeapon();
        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 0, 0);
        map.setPlayerPosition(p4, 1, 3);
        ArrayList<ArrayList<Damage>> tester = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(1);
            tester.get(i).get(0).setMarks(2);
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(1).get(0).setTarget(p4);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 1);
        map.setPlayerPosition(p3, 2, 3);
        map.setPlayerPosition(p4, 1, 2);
        tester.clear();
        for (int i = 0; i < 3; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(1);
            tester.get(i).get(0).setMarks(2);
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(1).get(0).setTarget(p3);
        tester.get(2).get(0).setTarget(p4);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 0);
        map.setPlayerPosition(p3, 2, 0);
        map.setPlayerPosition(p4, 1, 1);
        tester.clear();
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 1);
        map.setPlayerPosition(p3, 2, 3);
        map.setPlayerPosition(p4, 1, 2);
        tester.clear();
        for (int i = 0; i < 7; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setMarks(1);
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(1).get(0).setTarget(p3);
        tester.get(2).get(0).setTarget(p4);
        tester.get(3).get(0).setTarget(p2);
        tester.get(3).add(new Damage());
        tester.get(3).get(1).setTarget(p3);
        tester.get(3).get(1).setMarks(1);
        tester.get(4).get(0).setTarget(p2);
        tester.get(4).add(new Damage());
        tester.get(4).get(1).setTarget(p4);
        tester.get(4).get(1).setMarks(1);
        tester.get(5).get(0).setTarget(p3);
        tester.get(5).add(new Damage());
        tester.get(5).get(1).setTarget(p4);
        tester.get(5).get(1).setMarks(1);
        tester.get(6).get(0).setTarget(p2);
        tester.get(6).add(new Damage());
        tester.get(6).get(1).setTarget(p3);
        tester.get(6).get(1).setMarks(1);
        tester.get(6).add(new Damage());
        tester.get(6).get(2).setTarget(p4);
        tester.get(6).get(2).setMarks(1);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 0);
        map.setPlayerPosition(p3, 2, 0);
        map.setPlayerPosition(p4, 1, 1);
        tester.clear();
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));
    }

    /**
     * Tests the usage of the Shotgun weapon, in both the modalities and in different situations
     *
     * @see GameLogic#useEffect(Player, Effect, Weapon, ArrayList)
     */
    @Test
    public void testWeaponShotgun() {
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("Shotgun"))) w = decks.drawWeapon();

        map.setPlayerPosition(p1, 1, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 0, 0);
        map.setPlayerPosition(p4, 1, 3);
        ArrayList<ArrayList<Damage>> tester = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(3);
            tester.get(i).get(0).setTarget(p4);
        }
        tester.get(0).get(0).setPosition(map.getCell(1, 2));
        tester.get(1).get(0).setPosition(map.getCell(0, 3));
        tester.get(2).get(0).setPosition(map.getCell(1, 3));
        tester.get(3).get(0).setPosition(map.getCell(2, 3));
        tester.sort(Comparator.comparing(a -> a.get(0)));
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 2, 3);
        map.setPlayerPosition(p3, 0, 0);
        map.setPlayerPosition(p4, 2, 3);
        tester.clear();
        for (int i = 0; i < 6; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(3);
            if (i < 3) tester.get(i).get(0).setTarget(p2);
            else tester.get(i).get(0).setTarget(p4);
        }
        tester.get(0).get(0).setPosition(map.getCell(1, 3));
        tester.get(1).get(0).setPosition(map.getCell(2, 3));
        tester.get(2).get(0).setPosition(map.getCell(2, 2));
        tester.get(3).get(0).setPosition(map.getCell(1, 3));
        tester.get(4).get(0).setPosition(map.getCell(2, 3));
        tester.get(5).get(0).setPosition(map.getCell(2, 2));
        tester.sort(Comparator.comparing(a -> a.get(0)));
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 3);
        map.setPlayerPosition(p3, 0, 0);
        map.setPlayerPosition(p4, 0, 3);
        tester.clear();
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 1, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 0, 0);
        map.setPlayerPosition(p4, 1, 3);
        tester.clear();
        tester.add(new ArrayList<>());
        tester.get(0).add(new Damage());
        tester.get(0).get(0).setDamage(2);
        tester.get(0).get(0).setTarget(p2);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 3);
        map.setPlayerPosition(p3, 2, 2);
        map.setPlayerPosition(p4, 2, 2);
        tester.clear();
        for (int i = 0; i < 3; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(2);
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(1).get(0).setTarget(p3);
        tester.get(2).get(0).setTarget(p4);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 2, 3);
        map.setPlayerPosition(p3, 0, 0);
        map.setPlayerPosition(p4, 0, 3);
        tester.clear();
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));
    }

    /**
     * Tests the usage of the Power Glove weapon, in both the modalities and in different situations
     *
     * @see GameLogic#useEffect(Player, Effect, Weapon, ArrayList)
     */
    @Test
    public void testWeaponPowerGlove() {
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("Power Glove"))) w = decks.drawWeapon();

        map.setPlayerPosition(p1, 1, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 0, 0);
        map.setPlayerPosition(p4, 1, 3);
        ArrayList<ArrayList<Damage>> tester = new ArrayList<>();
        tester.add(new ArrayList<>());
        tester.get(0).add(new Damage());
        tester.get(0).get(0).setDamage(1);
        tester.get(0).get(0).setTarget(p2);
        tester.get(0).get(0).setMarks(2);
        tester.get(0).add(new Damage());
        tester.get(0).get(1).setPosition(map.getCell(1, 2));
        tester.get(0).get(1).setTarget(p1);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 2);
        map.setPlayerPosition(p3, 1, 3);
        map.setPlayerPosition(p4, 1, 3);
        tester.clear();
        for (int i = 0; i < 3; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(1);
            tester.get(i).get(0).setMarks(2);
            tester.get(i).add(new Damage());
            tester.get(i).get(1).setTarget(p1);
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(1).get(0).setTarget(p3);
        tester.get(2).get(0).setTarget(p4);
        tester.get(0).get(1).setPosition(map.getCell(0, 2));
        tester.get(1).get(1).setPosition(map.getCell(1, 3));
        tester.get(2).get(1).setPosition(map.getCell(1, 3));
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 1, 1);
        map.setPlayerPosition(p2, 1, 1);
        map.setPlayerPosition(p3, 1, 2);
        map.setPlayerPosition(p4, 1, 0);
        tester.clear();
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 1);
        map.setPlayerPosition(p3, 1, 3);
        map.setPlayerPosition(p4, 2, 3);
        tester.clear();
        for (int i = 0; i < 2; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(2);
            tester.get(i).get(0).setTarget(p3);
            tester.get(i).add(new Damage());
            if (i == 0) {
                tester.get(i).get(1).setTarget(p1);
                tester.get(i).get(1).setPosition(map.getCellFromPlayer(p3));
            } else {
                tester.get(i).get(1).setTarget(p4);
                tester.get(i).get(1).setDamage(2);
                tester.get(i).add(new Damage());
                tester.get(i).get(2).setTarget(p1);
                tester.get(i).get(2).setPosition(map.getCellFromPlayer(p4));
            }
        }
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 2);
        map.setPlayerPosition(p3, 1, 3);
        map.setPlayerPosition(p4, 2, 3);
        tester.clear();
        for (int i = 0; i < 3; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(2);
            tester.get(i).get(0).setTarget(p3);
            tester.get(i).add(new Damage());
            if (i == 0) {

                tester.get(i).get(0).setTarget(p2);
                tester.get(i).get(1).setTarget(p1);
                tester.get(i).get(1).setPosition(map.getCellFromPlayer(p2));
            } else if (i == 1) {
                tester.get(i).get(1).setTarget(p1);
                tester.get(i).get(1).setPosition(map.getCellFromPlayer(p3));
            } else {
                tester.get(i).get(1).setTarget(p4);
                tester.get(i).get(1).setDamage(2);
                tester.get(i).add(new Damage());
                tester.get(i).get(2).setTarget(p1);
                tester.get(i).get(2).setPosition(map.getCellFromPlayer(p4));
            }
        }
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));

        map.setPlayerPosition(p1, 1, 1);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 1, 1);
        map.setPlayerPosition(p4, 1, 0);
        tester.clear();
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));
    }

    /**
     * Tests the usage of the Shockwave weapon, in both the modalities and in different situations
     *
     * @see GameLogic#useEffect(Player, Effect, Weapon, ArrayList)
     */
    @Test
    public void testWeaponShockwave() {
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("Shockwave"))) w = decks.drawWeapon();

        map.setPlayerPosition(p1, 1, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 1, 2);
        map.setPlayerPosition(p4, 2, 3);
        ArrayList<ArrayList<Damage>> tester = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(1);
            if (i > 2) {
                tester.get(i).add(new Damage());
                tester.get(i).get(1).setDamage(1);
            }
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(1).get(0).setTarget(p3);
        tester.get(2).get(0).setTarget(p4);
        tester.get(3).get(0).setTarget(p2);
        tester.get(4).get(0).setTarget(p3);
        tester.get(3).get(1).setTarget(p4);
        tester.get(4).get(1).setTarget(p4);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 1, 1);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 0, 1);
        map.setPlayerPosition(p4, 2, 1);
        tester.clear();
        for (int i = 0; i < 3; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(1);
            if (i > 1) {
                tester.get(i).add(new Damage());
                tester.get(i).get(1).setDamage(1);
            }
        }
        tester.get(0).get(0).setTarget(p3);
        tester.get(1).get(0).setTarget(p4);
        tester.get(2).get(0).setTarget(p3);
        tester.get(2).get(1).setTarget(p4);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 1, 1);
        map.setPlayerPosition(p2, 1, 1);
        map.setPlayerPosition(p3, 1, 0);
        map.setPlayerPosition(p4, 1, 2);
        tester.clear();
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 1, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 1, 2);
        map.setPlayerPosition(p4, 2, 3);
        tester.clear();
        tester.add(new ArrayList<>());
        for (int i = 0; i < 3; i++) {
            tester.get(0).add(new Damage());
            tester.get(0).get(i).setDamage(1);
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(0).get(1).setTarget(p3);
        tester.get(0).get(2).setTarget(p4);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));

        map.setPlayerPosition(p1, 1, 1);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 0, 1);
        map.setPlayerPosition(p4, 2, 1);
        tester.clear();
        tester.add(new ArrayList<>());
        for (int i = 0; i < 2; i++) {
            tester.get(0).add(new Damage());
            tester.get(0).get(i).setDamage(1);
        }
        tester.get(0).get(0).setTarget(p3);
        tester.get(0).get(1).setTarget(p4);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));

        map.setPlayerPosition(p1, 1, 1);
        map.setPlayerPosition(p2, 1, 1);
        map.setPlayerPosition(p3, 1, 0);
        map.setPlayerPosition(p4, 1, 2);
        tester.clear();
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));
    }

    /**
     * Tests the usage of the Sledgehammer weapon, in both the modalities and in different situations
     *
     * @see GameLogic#useEffect(Player, Effect, Weapon, ArrayList)
     */
    @Test
    public void testWeaponSledgehammer() {
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("Sledgehammer"))) w = decks.drawWeapon();

        map.setPlayerPosition(p1, 1, 3);
        map.setPlayerPosition(p2, 1, 3);
        map.setPlayerPosition(p3, 1, 3);
        map.setPlayerPosition(p4, 1, 3);
        ArrayList<ArrayList<Damage>> tester = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(2);
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(1).get(0).setTarget(p3);
        tester.get(2).get(0).setTarget(p4);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 1, 1);
        map.setPlayerPosition(p2, 0, 1);
        map.setPlayerPosition(p3, 1, 1);
        map.setPlayerPosition(p4, 2, 1);
        tester.clear();
        tester.add(new ArrayList<>());
        tester.get(0).add(new Damage());
        tester.get(0).get(0).setDamage(2);
        tester.get(0).get(0).setTarget(p3);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 1, 1);
        map.setPlayerPosition(p3, 1, 0);
        map.setPlayerPosition(p4, 1, 2);
        tester.clear();
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 1, 1);
        map.setPlayerPosition(p2, 1, 1);
        map.setPlayerPosition(p3, 1, 1);
        map.setPlayerPosition(p4, 2, 1);
        tester.clear();
        for (int i = 0; i < 6; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(3);
            tester.get(i).get(0).setTarget((i < 3) ? p2 : p3);
        }
        tester.get(0).get(0).setPosition(map.getCell(0, 1));
        tester.get(1).get(0).setPosition(map.getCell(1, 1));
        tester.get(2).get(0).setPosition(map.getCell(2, 1));
        tester.get(3).get(0).setPosition(map.getCell(0, 1));
        tester.get(4).get(0).setPosition(map.getCell(1, 1));
        tester.get(5).get(0).setPosition(map.getCell(2, 1));
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 2, 3);
        map.setPlayerPosition(p3, 1, 1);
        map.setPlayerPosition(p4, 2, 3);
        tester.clear();
        for (int i = 0; i < 10; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(3);
            tester.get(i).get(0).setTarget((i < 5) ? p2 : p4);
        }
        tester.get(0).get(0).setPosition(map.getCell(0, 3));
        tester.get(1).get(0).setPosition(map.getCell(1, 3));
        tester.get(2).get(0).setPosition(map.getCell(2, 1));
        tester.get(3).get(0).setPosition(map.getCell(2, 2));
        tester.get(4).get(0).setPosition(map.getCell(2, 3));
        tester.get(5).get(0).setPosition(map.getCell(0, 3));
        tester.get(6).get(0).setPosition(map.getCell(1, 3));
        tester.get(7).get(0).setPosition(map.getCell(2, 1));
        tester.get(8).get(0).setPosition(map.getCell(2, 2));
        tester.get(9).get(0).setPosition(map.getCell(2, 3));
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));

        map.setPlayerPosition(p1, 1, 1);
        map.setPlayerPosition(p2, 0, 1);
        map.setPlayerPosition(p3, 1, 2);
        map.setPlayerPosition(p4, 2, 1);
        tester.clear();
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));
    }

    /**
     * Tests the usage of the Lock Rifle weapon and all of its effects
     *
     * @see GameLogic#useEffect(Player, Effect, Weapon, ArrayList)
     */
    @Test
    public void testWeaponLockRifle() {
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("Lock Rifle"))) w = decks.drawWeapon();

        map.setPlayerPosition(p1, 1, 3);
        map.setPlayerPosition(p2, 1, 3);
        map.setPlayerPosition(p3, 1, 3);
        map.setPlayerPosition(p4, 1, 3);
        ArrayList<ArrayList<Damage>> tester = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(2);
            tester.get(i).get(0).setMarks(1);
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(1).get(0).setTarget(p3);
        tester.get(2).get(0).setTarget(p4);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 1, 3);
        map.setPlayerPosition(p2, 0, 0);
        map.setPlayerPosition(p3, 1, 0);
        map.setPlayerPosition(p4, 2, 0);
        tester.clear();
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 1, 3);
        map.setPlayerPosition(p2, 1, 3);
        map.setPlayerPosition(p3, 1, 3);
        map.setPlayerPosition(p4, 1, 3);
        ArrayList<Damage> forPotentiableWeapon = gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null).get(0);
        tester.clear();
        for (int i = 0; i < 2; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setMarks(1);
            tester.get(i).get(0).setTarget(i == 0 ? p3 : p4);
        }
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, forPotentiableWeapon));

        map.setPlayerPosition(p1, 1, 3);
        map.setPlayerPosition(p2, 1, 3);
        map.setPlayerPosition(p3, 0, 0);
        map.setPlayerPosition(p4, 0, 0);
        forPotentiableWeapon = gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null).get(0);
        tester.clear();
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, forPotentiableWeapon));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 1);
        map.setPlayerPosition(p3, 0, 2);
        map.setPlayerPosition(p4, 1, 3);
        forPotentiableWeapon = gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null).get(1);
        tester.clear();
        for (int i = 0; i < 2; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setMarks(1);
            tester.get(i).get(0).setTarget(i == 0 ? p2 : p4);
        }
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, forPotentiableWeapon));
    }

    /**
     * Tests the usage of the Machine Gun weapon and all of its effects
     *
     * @see GameLogic#useEffect(Player, Effect, Weapon, ArrayList)
     */
    @Test
    public void testWeaponMachineGun() {
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("Machine Gun"))) w = decks.drawWeapon();

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 1);
        map.setPlayerPosition(p3, 0, 3);
        map.setPlayerPosition(p4, 2, 3);
        ArrayList<ArrayList<Damage>> tester = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(1);
            if (i > 2) {
                tester.get(i).add(new Damage());
                tester.get(i).get(1).setDamage(1);
            }
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(1).get(0).setTarget(p3);
        tester.get(2).get(0).setTarget(p4);
        tester.get(3).get(0).setTarget(p2);
        tester.get(4).get(0).setTarget(p2);
        tester.get(5).get(0).setTarget(p3);
        tester.get(3).get(1).setTarget(p3);
        tester.get(4).get(1).setTarget(p4);
        tester.get(5).get(1).setTarget(p4);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 1, 1);
        map.setPlayerPosition(p2, 0, 0);
        map.setPlayerPosition(p3, 0, 3);
        map.setPlayerPosition(p4, 2, 3);
        tester.clear();
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 1);
        map.setPlayerPosition(p3, 0, 2);
        map.setPlayerPosition(p4, 1, 3);
        ArrayList<Damage> forPotentiableWeapon = gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null).get(3);
        tester.clear();
        ArrayList<ArrayList<Damage>> forOrder = gameLogicTest.useEffect(p1, w.getEffects().get(1), w, forPotentiableWeapon);
        for (int i = 0; i < 2; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(1);
            tester.get(i).get(0).setTarget(forOrder.get(0).get(0).getTarget() == p2 ? p2 : p3);
        }
        tester.get(1).get(0).setTarget(tester.get(0).get(0).getTarget() == p2 ? p3 : p2);
        assertEquals(tester, forOrder);

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 1);
        map.setPlayerPosition(p3, 0, 2);
        map.setPlayerPosition(p4, 1, 3);
        forPotentiableWeapon = gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null).get(3);
        forPotentiableWeapon.addAll(gameLogicTest.useEffect(p1, w.getEffects().get(1), w, forPotentiableWeapon).get(0));
        Player alreadyShot = forPotentiableWeapon.get(2).getTarget();
        Player toShoot = (alreadyShot == p2) ? p3 : p2;
        tester.clear();
        for (int i = 0; i < 3; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(1);
            tester.get(i).get(0).setTarget((i == 1) ? p4 : toShoot);
            if (i == 2) {
                tester.get(i).add(new Damage());
                tester.get(i).get(1).setTarget(p4);
                tester.get(i).get(1).setDamage(1);
            }
        }
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(2), w, forPotentiableWeapon));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 1);
        map.setPlayerPosition(p3, 0, 2);
        map.setPlayerPosition(p4, 1, 3);
        forPotentiableWeapon = gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null).get(3);
        tester.clear();
        for (int i = 0; i < 5; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(1);
            if (i > 2) {
                tester.get(i).add(new Damage());
                tester.get(i).get(1).setTarget(p4);
                tester.get(i).get(1).setDamage(1);
            }
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(1).get(0).setTarget(p3);
        tester.get(2).get(0).setTarget(p4);
        tester.get(3).get(0).setTarget(p2);
        tester.get(4).get(0).setTarget(p3);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(2), w, forPotentiableWeapon));
    }

    /**
     * Tests an exception when the Machine Gun weapon's optional effects are used in a wrong way
     * @see GameLogic#useEffect(Player, Effect, Weapon, ArrayList)
     */
    @Test (expected = NullPointerException.class)
    public void testMachineGunNullPointerExceptionEffect1() {
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("Machine Gun"))) w = decks.drawWeapon();
        gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null);
    }

    /**
     * Tests an exception when the Machine Gun weapon's optional effects are used in a wrong way
     * @see GameLogic#useEffect(Player, Effect, Weapon, ArrayList)
     */
    @Test (expected = RuntimeException.class)
    public void testMachineGunRuntimeExceptionEffect1() {
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("Machine Gun"))) w = decks.drawWeapon();
        ArrayList<Damage> forPotentiableWeapon = new ArrayList<>();
        gameLogicTest.useEffect(p1, w.getEffects().get(1), w, forPotentiableWeapon);
    }

    /**
     * Tests an exception when the Machine Gun weapon's optional effects are used in a wrong way
     * @see GameLogic#useEffect(Player, Effect, Weapon, ArrayList)
     */
    @Test (expected = NullPointerException.class)
    public void testMachineGunNullPointerExceptionEffect2() {
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("Machine Gun"))) w = decks.drawWeapon();
        gameLogicTest.useEffect(p1, w.getEffects().get(2), w, null);
    }

    /**
     * Tests an exception when the Machine Gun weapon's optional effects are used in a wrong way
     * @see GameLogic#useEffect(Player, Effect, Weapon, ArrayList)
     */
    @Test (expected = RuntimeException.class)
    public void testMachineGunRuntimeExceptionEffect2() {
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("Machine Gun"))) w = decks.drawWeapon();
        ArrayList<Damage> forPotentiableWeapon = new ArrayList<>();
        gameLogicTest.useEffect(p1, w.getEffects().get(2), w, forPotentiableWeapon);
    }

    /**
     * Tests the usage of the T.H.O.R. weapon and all of its effects
     *
     * @see GameLogic#useEffect(Player, Effect, Weapon, ArrayList)
     */
    @Test
    public void testWeaponTHOR() {
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("T.H.O.R."))) w = decks.drawWeapon();

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 1);
        map.setPlayerPosition(p3, 1, 0);
        map.setPlayerPosition(p4, 2, 1);
        ArrayList<ArrayList<Damage>> tester = new ArrayList<>();
        tester.add(new ArrayList<>());
        tester.get(0).add(new Damage());
        tester.get(0).get(0).setTarget(p2);
        tester.get(0).get(0).setDamage(2);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 1, 3);
        map.setPlayerPosition(p4, 2, 2);
        tester.clear();
        for (int i = 0; i < 3; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(2);
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(1).get(0).setTarget(p3);
        tester.get(2).get(0).setTarget(p4);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 1);
        map.setPlayerPosition(p3, 1, 0);
        map.setPlayerPosition(p4, 2, 0);
        tester.clear();
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 1);
        map.setPlayerPosition(p3, 1, 0);
        map.setPlayerPosition(p4, 2, 1);
        tester.clear();
        ArrayList<Damage> forPotentiableWeapon = gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null).get(0);
        tester.add(new ArrayList<>());
        tester.get(0).add(new Damage());
        tester.get(0).get(0).setTarget(p3);
        tester.get(0).get(0).setDamage(1);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, forPotentiableWeapon));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 1, 3);
        map.setPlayerPosition(p4, 2, 2);
        tester.clear();
        forPotentiableWeapon = gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null).get(0);
        for (int i = 0; i < 2; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(1);
        }
        tester.get(0).get(0).setTarget(p3);
        tester.get(1).get(0).setTarget(p4);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, forPotentiableWeapon));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 0, 0);
        map.setPlayerPosition(p4, 0, 0);
        tester.clear();
        forPotentiableWeapon = gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null).get(0);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, forPotentiableWeapon));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 1);
        map.setPlayerPosition(p3, 1, 0);
        map.setPlayerPosition(p4, 2, 1);
        tester.clear();
        forPotentiableWeapon = gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null).get(0);
        forPotentiableWeapon.addAll(gameLogicTest.useEffect(p1, w.getEffects().get(1), w, forPotentiableWeapon).get(0));
        tester.add(new ArrayList<>());
        tester.get(0).add(new Damage());
        tester.get(0).get(0).setTarget(p4);
        tester.get(0).get(0).setDamage(2);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(2), w, forPotentiableWeapon));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 1, 3);
        map.setPlayerPosition(p4, 2, 2);
        tester.clear();
        forPotentiableWeapon = gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null).get(0);
        forPotentiableWeapon.addAll(gameLogicTest.useEffect(p1, w.getEffects().get(1), w, forPotentiableWeapon).get(0));
        for (int i = 0; i < 1; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(2);
        }
        tester.get(0).get(0).setTarget(p4);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(2), w, forPotentiableWeapon));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 1, 3);
        map.setPlayerPosition(p4, 0, 0);
        tester.clear();
        forPotentiableWeapon = gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null).get(0);
        forPotentiableWeapon.addAll(gameLogicTest.useEffect(p1, w.getEffects().get(1), w, forPotentiableWeapon).get(0));
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(2), w, forPotentiableWeapon));
    }

    /**
     * Tests an exception when the T.H.O.R. weapon's optional effects are used in a wrong way
     * @see GameLogic#useEffect(Player, Effect, Weapon, ArrayList)
     */
    @Test (expected = NullPointerException.class)
    public void testTHORNullPointerExceptionEffect1() {
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("T.H.O.R."))) w = decks.drawWeapon();
        gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null);
    }

    /**
     * Tests an exception when the T.H.O.R. weapon's optional effects are used in a wrong way
     * @see GameLogic#useEffect(Player, Effect, Weapon, ArrayList)
     */
    @Test (expected = RuntimeException.class)
    public void testTHORRuntimeExceptionEffect1() {
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("T.H.O.R."))) w = decks.drawWeapon();
        ArrayList<Damage> forPotentiableWeapon = new ArrayList<>();
        gameLogicTest.useEffect(p1, w.getEffects().get(1), w, forPotentiableWeapon);
    }

    /**
     * Tests an exception when the T.H.O.R. weapon's optional effects are used in a wrong way
     * @see GameLogic#useEffect(Player, Effect, Weapon, ArrayList)
     */
    @Test (expected = NullPointerException.class)
    public void testTHORNullPointerExceptionEffect2() {
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("T.H.O.R."))) w = decks.drawWeapon();
        gameLogicTest.useEffect(p1, w.getEffects().get(2), w, null);
    }

    /**
     * Tests an exception when the T.H.O.R. weapon's optional effects are used in a wrong way
     * @see GameLogic#useEffect(Player, Effect, Weapon, ArrayList)
     */
    @Test (expected = RuntimeException.class)
    public void testTHORRuntimeExceptionEffect2() {
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("T.H.O.R."))) w = decks.drawWeapon();
        ArrayList<Damage> forPotentiableWeapon = new ArrayList<>();
        gameLogicTest.useEffect(p1, w.getEffects().get(2), w, forPotentiableWeapon);
    }

    /**
     * Tests the usage of the Plasma Gun weapon and all of its effects
     *
     * @see GameLogic#useEffect(Player, Effect, Weapon, ArrayList)
     */
    @Test
    public void testWeaponPlasmaGun() {
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("Plasma Gun"))) w = decks.drawWeapon();

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 1);
        map.setPlayerPosition(p3, 1, 0);
        map.setPlayerPosition(p4, 2, 1);
        ArrayList<ArrayList<Damage>> tester = new ArrayList<>();
        tester.add(new ArrayList<>());
        tester.get(0).add(new Damage());
        tester.get(0).get(0).setTarget(p2);
        tester.get(0).get(0).setDamage(2);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 1, 3);
        map.setPlayerPosition(p4, 2, 2);
        tester.clear();
        for (int i = 0; i < 3; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(2);
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(1).get(0).setTarget(p3);
        tester.get(2).get(0).setTarget(p4);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 1);
        map.setPlayerPosition(p3, 1, 0);
        map.setPlayerPosition(p4, 2, 0);
        tester.clear();
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 1, 1);
        map.setPlayerPosition(p2, 0, 0);
        map.setPlayerPosition(p3, 0, 0);
        map.setPlayerPosition(p4, 0, 0);
        tester.clear();
        for (int i = 0; i < 6; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setTarget(p1);
        }
        tester.get(0).get(0).setPosition(board.getMap().getCell(0, 0));
        tester.get(1).get(0).setPosition(board.getMap().getCell(0, 1));
        tester.get(2).get(0).setPosition(board.getMap().getCell(0, 2));
        tester.get(3).get(0).setPosition(board.getMap().getCell(2, 0));
        tester.get(4).get(0).setPosition(board.getMap().getCell(2, 1));
        tester.get(5).get(0).setPosition(board.getMap().getCell(2, 2));
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));

        map.setPlayerPosition(p1, 1, 2);
        map.setPlayerPosition(p2, 0, 0);
        map.setPlayerPosition(p3, 0, 0);
        map.setPlayerPosition(p4, 0, 0);
        tester.clear();
        for (int i = 0; i < 7; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setTarget(p1);
        }
        tester.get(0).get(0).setPosition(board.getMap().getCell(0, 1));
        tester.get(1).get(0).setPosition(board.getMap().getCell(0, 2));
        tester.get(2).get(0).setPosition(board.getMap().getCell(0, 3));
        tester.get(3).get(0).setPosition(board.getMap().getCell(1, 3));
        tester.get(4).get(0).setPosition(board.getMap().getCell(2, 1));
        tester.get(5).get(0).setPosition(board.getMap().getCell(2, 2));
        tester.get(6).get(0).setPosition(board.getMap().getCell(2, 3));
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));

        map.setPlayerPosition(p1, 0, 0);
        map.setPlayerPosition(p2, 0, 0);
        map.setPlayerPosition(p3, 0, 0);
        map.setPlayerPosition(p4, 0, 0);
        tester.clear();
        for (int i = 0; i < 5; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setTarget(p1);
        }
        tester.get(0).get(0).setPosition(board.getMap().getCell(0, 1));
        tester.get(1).get(0).setPosition(board.getMap().getCell(0, 2));
        tester.get(2).get(0).setPosition(board.getMap().getCell(1, 0));
        tester.get(3).get(0).setPosition(board.getMap().getCell(1, 1));
        tester.get(4).get(0).setPosition(board.getMap().getCell(2, 0));
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 1);
        map.setPlayerPosition(p3, 1, 0);
        map.setPlayerPosition(p4, 2, 1);
        tester.clear();
        ArrayList<Damage> forPotentiableWeapon = gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null).get(0);
        tester.add(new ArrayList<>());
        tester.get(0).add(new Damage());
        tester.get(0).get(0).setTarget(p2);
        tester.get(0).get(0).setDamage(1);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(2), w, forPotentiableWeapon));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 1, 3);
        map.setPlayerPosition(p4, 2, 2);
        tester.clear();
        forPotentiableWeapon = gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null).get(1);
        tester.add(new ArrayList<>());
        tester.get(0).add(new Damage());
        tester.get(0).get(0).setTarget(p3);
        tester.get(0).get(0).setDamage(1);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(2), w, forPotentiableWeapon));
    }

    /**
     * Tests an exception when the Plasma Gun weapon's optional effects are used in a wrong way
     * @see GameLogic#useEffect(Player, Effect, Weapon, ArrayList)
     */
    @Test (expected = NullPointerException.class)
    public void testPlasmaGunNullPointerException() {
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("Plasma Gun"))) w = decks.drawWeapon();
        gameLogicTest.useEffect(p1, w.getEffects().get(2), w, null);
    }

    /**
     * Tests an exception when the Plasma Gun weapon's optional effects are used in a wrong way
     * @see GameLogic#useEffect(Player, Effect, Weapon, ArrayList)
     */
    @Test (expected = RuntimeException.class)
    public void testPlasmaGunRuntimeException() {
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("Plasma Gun"))) w = decks.drawWeapon();
        ArrayList<Damage> forPotentiableWeapon = new ArrayList<>();
        gameLogicTest.useEffect(p1, w.getEffects().get(2), w, forPotentiableWeapon);
    }

    /**
     * Tests the usage of the Vortex Cannon weapon and all of its effects
     *
     * @see GameLogic#useEffect(Player, Effect, Weapon, ArrayList)
     */
    @Test
    public void testWeaponVortexCannon() {
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("Vortex Cannon"))) w = decks.drawWeapon();

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 1, 2);
        map.setPlayerPosition(p4, 0, 0);
        ArrayList<ArrayList<Damage>> tester = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(2);
            tester.get(i).get(0).setTarget((i < 3) ? p2 : p3);
        }
        tester.get(0).get(0).setPosition(map.getCell(1, 2));
        tester.get(1).get(0).setPosition(map.getCell(1, 3));
        tester.get(2).get(0).setPosition(map.getCell(2, 2));
        tester.get(3).get(0).setPosition(map.getCell(1, 2));
        tester.get(4).get(0).setPosition(map.getCell(1, 3));
        tester.get(5).get(0).setPosition(map.getCell(2, 2));
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 3);
        map.setPlayerPosition(p3, 2, 2);
        map.setPlayerPosition(p4, 0, 0);
        tester.clear();
        for (int i = 0; i < 4; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(2);
            tester.get(i).get(0).setTarget((i < 2) ? p2 : p3);
        }
        tester.get(0).get(0).setPosition(map.getCell(1, 2));
        tester.get(1).get(0).setPosition(map.getCell(1, 3));
        tester.get(2).get(0).setPosition(map.getCell(1, 2));
        tester.get(3).get(0).setPosition(map.getCell(2, 2));
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 0, 0);
        map.setPlayerPosition(p2, 0, 3);
        map.setPlayerPosition(p3, 2, 0);
        map.setPlayerPosition(p4, 0, 0);
        tester.clear();
        for (int i = 0; i < 4; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(2);
            tester.get(i).get(0).setTarget((i > 1) ? p4 : p3);
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(0).get(0).setPosition(map.getCell(0, 2));
        tester.get(1).get(0).setPosition(map.getCell(1, 0));
        tester.get(2).get(0).setPosition(map.getCell(0, 1));
        tester.get(3).get(0).setPosition(map.getCell(1, 0));
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 0, 0);
        map.setPlayerPosition(p2, 1, 3);
        map.setPlayerPosition(p3, 2, 1);
        map.setPlayerPosition(p4, 2, 2);
        tester.clear();
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 3);
        map.setPlayerPosition(p3, 2, 2);
        map.setPlayerPosition(p4, 1, 3);
        ArrayList<Damage> forPotentiableWeapon = gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null).get(0);
        tester.clear();
        for (int i = 0; i < 2; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(1);
            tester.get(i).get(0).setTarget((i == 0) ? p3 : p4);
        }
        tester.get(0).get(0).setPosition(map.getCell(1, 2));
        tester.get(1).get(0).setPosition(map.getCell(1, 2));
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, forPotentiableWeapon));
    }

    /**
     * Tests the usage of the Grenade Launcher weapon and all of its effects
     *
     * @see GameLogic#useEffect(Player, Effect, Weapon, ArrayList)
     */
    @Test
    public void testWeaponGrenadeLauncher() {
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("Grenade Launcher"))) w = decks.drawWeapon();

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 1, 2);
        map.setPlayerPosition(p4, 0, 0);
        ArrayList<ArrayList<Damage>> tester = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(1);
            tester.get(i).get(0).setTarget((i < 4) ? p2 : p3);
        }
        tester.get(0).get(0).setPosition(map.getCell(0, 2));
        tester.get(1).get(0).setPosition(map.getCell(1, 2));
        tester.get(2).get(0).setPosition(map.getCell(1, 3));
        tester.get(3).get(0).setPosition(map.getCell(2, 2));
        tester.get(4).get(0).setPosition(map.getCell(0, 2));
        tester.get(5).get(0).setPosition(map.getCell(1, 2));
        tester.get(6).get(0).setPosition(map.getCell(1, 3));
        tester.get(7).get(0).setPosition(map.getCell(2, 2));
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 1, 1);
        map.setPlayerPosition(p2, 1, 1);
        map.setPlayerPosition(p3, 2, 0);
        map.setPlayerPosition(p4, 0, 0);
        tester.clear();
        for (int i = 0; i < 6; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(1);
            tester.get(i).get(0).setTarget((i < 3) ? p2 : p3);
        }
        tester.get(0).get(0).setPosition(map.getCell(0, 1));
        tester.get(1).get(0).setPosition(map.getCell(1, 1));
        tester.get(2).get(0).setPosition(map.getCell(2, 1));
        tester.get(3).get(0).setPosition(map.getCell(1, 0));
        tester.get(4).get(0).setPosition(map.getCell(2, 0));
        tester.get(5).get(0).setPosition(map.getCell(2, 1));
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 1, 1);
        map.setPlayerPosition(p2, 0, 2);
        map.setPlayerPosition(p3, 2, 0);
        map.setPlayerPosition(p4, 0, 0);
        tester.clear();
        for (int i = 0; i < 7; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(1);
            tester.get(i).get(0).setTarget((i < 4) ? p2 : p3);
        }
        tester.get(0).get(0).setPosition(map.getCell(0, 1));
        tester.get(1).get(0).setPosition(map.getCell(0, 2));
        tester.get(2).get(0).setPosition(map.getCell(0, 3));
        tester.get(3).get(0).setPosition(map.getCell(1, 2));
        tester.get(4).get(0).setPosition(map.getCell(1, 0));
        tester.get(5).get(0).setPosition(map.getCell(2, 0));
        tester.get(6).get(0).setPosition(map.getCell(2, 1));
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 1);
        map.setPlayerPosition(p3, 1, 0);
        map.setPlayerPosition(p4, 0, 0);
        tester.clear();
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 1, 2);
        map.setPlayerPosition(p4, 1, 3);
        tester.clear();
        for (int i = 0; i < 2; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(1);
            if(i==0) {
                tester.get(i).add(new Damage());
                tester.get(i).get(1).setDamage(1);
            }
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(0).get(1).setTarget(p3);
        tester.get(1).get(0).setTarget(p4);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 3);
        map.setPlayerPosition(p3, 0, 3);
        map.setPlayerPosition(p4, 1, 3);
        tester.clear();
        for (int i = 0; i < 2; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(1);
            if(i==0) {
                tester.get(i).add(new Damage());
                tester.get(i).get(1).setDamage(1);
            }
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(0).get(1).setTarget(p3);
        tester.get(1).get(0).setTarget(p4);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 1);
        map.setPlayerPosition(p3, 0, 2);
        map.setPlayerPosition(p4, 1, 0);
        tester.clear();
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));
    }

    /**
     * Tests the usage of the Rocket Launcher weapon and all of its effects, except the "with rocket jump" one, as it is the same as the "with phase glide" effect of the "Plasma Gun" weapon
     *
     * @see GameLogic#useEffect(Player, Effect, Weapon, ArrayList)
     */
    @Test
    public void testWeaponRocketLauncher() {
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("Rocket Launcher"))) w = decks.drawWeapon();

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 1, 2);
        map.setPlayerPosition(p4, 0, 0);
        ArrayList<ArrayList<Damage>> tester = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(2);
            tester.get(i).get(0).setTarget((i < 4) ? p2 : p3);
        }
        tester.get(0).get(0).setPosition(map.getCell(0, 2));
        tester.get(1).get(0).setPosition(map.getCell(1, 2));
        tester.get(2).get(0).setPosition(map.getCell(1, 3));
        tester.get(3).get(0).setPosition(map.getCell(2, 2));
        tester.get(4).get(0).setPosition(map.getCell(0, 2));
        tester.get(5).get(0).setPosition(map.getCell(1, 2));
        tester.get(6).get(0).setPosition(map.getCell(1, 3));
        tester.get(7).get(0).setPosition(map.getCell(2, 2));
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 1, 1);
        map.setPlayerPosition(p2, 0, 2);
        map.setPlayerPosition(p3, 1, 1);
        map.setPlayerPosition(p4, 0, 0);
        tester.clear();
        for (int i = 0; i < 4; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(2);
            tester.get(i).get(0).setTarget(p2);
        }
        tester.get(0).get(0).setPosition(map.getCell(0, 1));
        tester.get(1).get(0).setPosition(map.getCell(0, 2));
        tester.get(2).get(0).setPosition(map.getCell(0, 3));
        tester.get(3).get(0).setPosition(map.getCell(1, 2));
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 1);
        map.setPlayerPosition(p3, 1, 0);
        map.setPlayerPosition(p4, 0, 0);
        tester.clear();
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 1, 2);
        map.setPlayerPosition(p4, 1, 2);
        tester.clear();
        ArrayList<Damage> forPotentiableWeapon = gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null).get(1);
        tester.add(new ArrayList<>());
        for (int i = 0; i < 3; i++) {
            tester.get(0).add(new Damage());
            tester.get(0).get(i).setDamage(1);
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(0).get(1).setTarget(p3);
        tester.get(0).get(2).setTarget(p4);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(2), w, forPotentiableWeapon));

        map.setPlayerPosition(p1, 1, 1);
        map.setPlayerPosition(p2, 0, 2);
        map.setPlayerPosition(p3, 0, 2);
        map.setPlayerPosition(p4, 0, 2);
        tester.clear();
        forPotentiableWeapon = gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null).get(0);
        tester.add(new ArrayList<>());
        for (int i = 0; i < 3; i++) {
            tester.get(0).add(new Damage());
            tester.get(0).get(i).setDamage(1);
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(0).get(1).setTarget(p3);
        tester.get(0).get(2).setTarget(p4);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(2), w, forPotentiableWeapon));
    }

    /**
     * Tests an exception when the Rocket Launcher weapon's optional effects are used in a wrong way
     * @see GameLogic#useEffect(Player, Effect, Weapon, ArrayList)
     */
    @Test (expected = NullPointerException.class)
    public void testRocketLauncherNullPointerException() {
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("Rocket Launcher"))) w = decks.drawWeapon();
        gameLogicTest.useEffect(p1, w.getEffects().get(2), w, null);
    }

    /**
     * Tests an exception when the Rocket Launcher weapon's optional effects are used in a wrong way
     * @see GameLogic#useEffect(Player, Effect, Weapon, ArrayList)
     */
    @Test (expected = RuntimeException.class)
    public void testRocketLauncherRuntimeException() {
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("Rocket Launcher"))) w = decks.drawWeapon();
        ArrayList<Damage> forPotentiableWeapon = new ArrayList<>();
        gameLogicTest.useEffect(p1, w.getEffects().get(2), w, forPotentiableWeapon);
    }

    /**
     * Tests the usage of the Rocket Launcher weapon and all of its effects
     *
     * @see GameLogic#useEffect(Player, Effect, Weapon, ArrayList)
     */
    @Test
    public void testWeaponCyberblade() {
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("Cyberblade"))) w = decks.drawWeapon();

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 2, 3);
        map.setPlayerPosition(p3, 2, 3);
        map.setPlayerPosition(p4, 2, 3);
        ArrayList<ArrayList<Damage>> tester = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(2);
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(1).get(0).setTarget(p3);
        tester.get(2).get(0).setTarget(p4);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 0, 0);
        map.setPlayerPosition(p3, 1, 1);
        map.setPlayerPosition(p4, 0, 1);
        tester.clear();
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null));

        map.setPlayerPosition(p1, 0, 0);
        map.setPlayerPosition(p2, 2, 3);
        map.setPlayerPosition(p3, 2, 3);
        map.setPlayerPosition(p4, 2, 3);
        tester.clear();
        for (int i = 0; i < 2; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setTarget(p1);
        }
        tester.get(0).get(0).setPosition(map.getCell(0,1));
        tester.get(1).get(0).setPosition(map.getCell(1,0));
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));

        map.setPlayerPosition(p1, 1, 1);
        map.setPlayerPosition(p2, 2, 3);
        map.setPlayerPosition(p3, 2, 3);
        map.setPlayerPosition(p4, 2, 3);
        tester.clear();
        for (int i = 0; i < 2; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setTarget(p1);
        }
        tester.get(0).get(0).setPosition(map.getCell(0,1));
        tester.get(1).get(0).setPosition(map.getCell(2,1));
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));

        map.setPlayerPosition(p1, 1, 2);
        map.setPlayerPosition(p2, 2, 3);
        map.setPlayerPosition(p3, 2, 3);
        map.setPlayerPosition(p4, 2, 3);
        tester.clear();
        for (int i = 0; i < 3; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setTarget(p1);
        }
        tester.get(0).get(0).setPosition(map.getCell(0,2));
        tester.get(1).get(0).setPosition(map.getCell(1,3));
        tester.get(2).get(0).setPosition(map.getCell(2,2));
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(1), w, null));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 2, 3);
        map.setPlayerPosition(p3, 2, 3);
        map.setPlayerPosition(p4, 2, 3);
        tester.clear();
        ArrayList<Damage> forPotentiableWeapon = gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null).get(0);
        for (int i = 0; i < 2; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(2);
        }
        tester.get(0).get(0).setTarget(p3);
        tester.get(1).get(0).setTarget(p4);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(2), w, forPotentiableWeapon));

        map.setPlayerPosition(p1, 0, 0);
        map.setPlayerPosition(p2, 0, 0);
        map.setPlayerPosition(p3, 0, 0);
        map.setPlayerPosition(p4, 0, 0);
        tester.clear();
        forPotentiableWeapon = gameLogicTest.useEffect(p1, w.getEffects().get(0), w, null).get(2);
        for (int i = 0; i < 2; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(2);
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(1).get(0).setTarget(p3);
        assertEquals(tester, gameLogicTest.useEffect(p1, w.getEffects().get(2), w, forPotentiableWeapon));
    }

    /**
     * Tests an exception when the Cyberblade weapon's optional effects are used in a wrong way
     * @see GameLogic#useEffect(Player, Effect, Weapon, ArrayList)
     */
    @Test (expected = NullPointerException.class)
    public void testCyberbladeNullPointerException() {
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("Cyberblade"))) w = decks.drawWeapon();
        gameLogicTest.useEffect(p1, w.getEffects().get(2), w, null);
    }

    /**
     * Tests an exception when the Cyberblade weapon's optional effects are used in a wrong way
     * @see GameLogic#useEffect(Player, Effect, Weapon, ArrayList)
     */
    @Test (expected = RuntimeException.class)
    public void testCyberbladeRuntimeException() {
        Weapon w = decks.drawWeapon();
        while (!(w.getName().equals("Cyberblade"))) w = decks.drawWeapon();
        ArrayList<Damage> forPotentiableWeapon = new ArrayList<>();
        gameLogicTest.useEffect(p1, w.getEffects().get(2), w, forPotentiableWeapon);
    }
}
