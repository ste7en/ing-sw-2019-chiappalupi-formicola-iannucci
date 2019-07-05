package it.polimi.ingsw.model;

import it.polimi.ingsw.model.board.Cell;
import it.polimi.ingsw.model.board.GameMap;
import it.polimi.ingsw.model.cards.Damage;
import it.polimi.ingsw.model.cards.Effect;
import it.polimi.ingsw.model.cards.PotentiableWeapon;
import it.polimi.ingsw.model.cards.Weapon;
import it.polimi.ingsw.model.player.Character;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.User;
import it.polimi.ingsw.model.utility.MapType;
import it.polimi.ingsw.model.utility.PlayerColor;
import org.junit.*;

import java.util.*;

import static org.junit.Assert.*;
import it.polimi.ingsw.controller.*;

/**
 * Test for {@link Weapon} class
 *
 * @author Daniele Chiappalupi
 */
public class WeaponTest {

    private Weapon weaponTester;
    private static DecksHandler decks;
    private static GameMap map;
    private static Player p1;
    private static Player p2;
    private static Player p3;
    private static Player p4;
    private static ArrayList<Player> players;

    /**
     * Initializes a decks handler where to draw sample weapons
     */
    @BeforeClass
    public static void setUpBeforeClass() {
        players = new ArrayList<>();
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
        players.add(p1);
        players.add(p2);
        players.add(p3);
        players.add(p4);
    }

    /**
     * Initializes the weaponTester before every testG
     */
    @Before
    public void setUp() {
        decks = new DecksHandler();
        weaponTester = decks.drawWeapon();
    }

    /**
     * Tests the generateTargetsCombination method in some real cases
     *
     * @see Weapon#generateTargetsCombinations(Effect, List, Player, GameMap)
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
        while (!(weaponTester.getName().equals("Lock Rifle"))) weaponTester = decks.drawWeapon();
        ArrayList<ArrayList<Player>> tester = new ArrayList<>();
        for (int i = 0; i < 4; i++) tester.add(i, new ArrayList<>());
        tester.get(0).add(p1);
        tester.get(1).add(p2);
        tester.get(2).add(p3);
        tester.get(3).add(p4);
        assertEquals(tester, weaponTester.generateTargetsCombinations(weaponTester.getEffects().get(0), p, p1, map));

        decks = new DecksHandler();
        weaponTester = decks.drawWeapon();
        while (!(weaponTester.getName().equals("Machine Gun"))) weaponTester = decks.drawWeapon();
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
        assertEquals(tester, weaponTester.generateTargetsCombinations(weaponTester.getEffects().get(0), p, p1, map));
    }

    /**
     * Tests the numberCombination method
     *
     * @see Weapon#generateTargetsCombinations(Effect, List, Player, GameMap)
     */
    @Test
    public void testCombinationsWithLowerValues() {
        Map<Integer, ArrayList<Integer>> box = weaponTester.combinationsWithLowerValues(4, 2);
        Map<Integer, ArrayList<Integer>> tester = new HashMap<>();
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
     * Tests the isLoaded() method, both with a positive and a negative test
     * @see Weapon#isLoaded()
     */
    @Test
    public void testIsLoaded() {
        weaponTester.reload();
        assertTrue(weaponTester.isLoaded());
        weaponTester.unload();
        assertFalse(weaponTester.isLoaded());
        weaponTester.reload();
        assertTrue(weaponTester.isLoaded());
    }

    /**
     * Tests the unload() method
     * @see Weapon#unload()
     */
    @Test
    public void testUnload() {
        weaponTester.unload();
        assertFalse(weaponTester.isLoaded());
    }

    /**
     * Tests the reload() method
     * @see Weapon#reload()
     */
    @Test
    public void testReload() {
        weaponTester.reload();
        assertTrue(weaponTester.isLoaded());
    }

    /**
     * Tests the usage of the Whisper weapon in different situations
     *
     * @see Weapon#generateTargetsCombinations(Effect, List, Player, GameMap)
     */
    @Test
    public void testWeaponWhisper() {
        while (!(weaponTester.getName().equals("Whisper"))) weaponTester = decks.drawWeapon();
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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 0);
        map.setPlayerPosition(p3, 2, 0);
        map.setPlayerPosition(p4, 1, 1);
        tester.clear();
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));
    }

    /**
     * Tests the usage of the Heatseeker weapon in different situations
     *
     * @see Weapon#useEffect(Player, Effect, List, GameMap, List)
     */
    @Test
    public void testWeaponHeatseeker() {
        while (!(weaponTester.getName().equals("Heatseeker"))) {
            weaponTester = decks.drawWeapon();
        }
        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 1, 1);
        map.setPlayerPosition(p3, 2, 3);
        map.setPlayerPosition(p4, 1, 2);
        ArrayList<ArrayList<Damage>> tester = new ArrayList<>();
        tester.add(new ArrayList<>());
        tester.get(0).add(new Damage());
        tester.get(0).get(0).setDamage(3);
        tester.get(0).get(0).setTarget(p2);
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));
    }

    /**
     * Tests the usage of the Electroscythe weapon, in both the modalities and in different situations
     *
     * @see Weapon#useEffect(Player, Effect, List, GameMap, List)
     */
    @Test
    public void testWeaponElectroscythe() {
        while (!(weaponTester.getName().equals("Electroscythe"))) weaponTester = decks.drawWeapon();
        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 1, 1);
        map.setPlayerPosition(p3, 0, 3);
        map.setPlayerPosition(p4, 1, 2);
        ArrayList<ArrayList<Damage>> tester = new ArrayList<>();
        tester.add(new ArrayList<>());
        tester.get(0).add(new Damage());
        tester.get(0).get(0).setDamage(1);
        tester.get(0).get(0).setTarget(p3);
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 1, 1);
        map.setPlayerPosition(p3, 0, 3);
        map.setPlayerPosition(p4, 1, 2);
        tester.clear();
        tester.add(new ArrayList<>());
        tester.get(0).add(new Damage());
        tester.get(0).get(0).setDamage(2);
        tester.get(0).get(0).setTarget(p3);
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));
    }

    /**
     * Tests the Compute Movements method
     *
     * @see Weapon#computeMovement(Effect, Player, Player, Cell, GameMap)
     */
    @Test
    public void testComputeMovements() {
        while (!(weaponTester.getName().equals("Tractor Beam"))) weaponTester = decks.drawWeapon();
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
        assertEquals(tester, weaponTester.computeMovement(weaponTester.getEffects().get(0), p1, p2, null, map));

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
        assertEquals(tester, weaponTester.computeMovement(weaponTester.getEffects().get(0), p1, p2, null, map));
    }

    /**
     * Tests the usage of the Tractor Beam weapon, in both the modalities and in different situations
     *
     * @see Weapon#useEffect(Player, Effect, List, GameMap, List)
     */
    @Test
    public void testWeaponTractorBeam() {
        while (!(weaponTester.getName().equals("Tractor Beam"))) weaponTester = decks.drawWeapon();

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 1, 2);
        map.setPlayerPosition(p4, 0, 0);
        ArrayList<ArrayList<Damage>> tester = new ArrayList<>();
        ArrayList<Cell> movementsP2 = weaponTester.computeMovement(weaponTester.getEffects().get(0), p1, p2, null, map);
        ArrayList<Cell> movementsP3 = weaponTester.computeMovement(weaponTester.getEffects().get(0), p1, p3, null, map);
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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

        map.setPlayerPosition(p1, 0, 0);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 2, 3);
        map.setPlayerPosition(p4, 2, 3);
        tester.clear();
        movements = weaponTester.computeMovement(weaponTester.getEffects().get(0), p1, p2, null, map);
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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));
    }

    /**
     * Tests the usage of the Furnace weapon, in both the modalities and in different situations
     *
     * @see Weapon#useEffect(Player, Effect, List, GameMap, List)
     */
    @Test
    public void testWeaponFurnace() {
        while (!(weaponTester.getName().equals("Furnace"))) weaponTester = decks.drawWeapon();

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 0, 2);
        map.setPlayerPosition(p3, 0, 3);
        map.setPlayerPosition(p4, 0, 2);
        tester.clear();
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 2, 2);
        map.setPlayerPosition(p3, 1, 2);
        map.setPlayerPosition(p4, 1, 3);
        tester.clear();
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));
    }

    /**
     * Tests the usage of the Furnace weapon, in both the modalities and in different situations
     *
     * @see Weapon#useEffect(Player, Effect, List, GameMap, List)
     */
    @Test
    public void testWeaponHellion() {
        while (!(weaponTester.getName().equals("Hellion"))) weaponTester = decks.drawWeapon();

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

        map.setPlayerPosition(p1, 0, 1);
        map.setPlayerPosition(p2, 0, 1);
        map.setPlayerPosition(p3, 2, 1);
        map.setPlayerPosition(p4, 2, 3);
        tester.clear();
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));

        map.setPlayerPosition(p1, 0, 1);
        map.setPlayerPosition(p2, 0, 1);
        map.setPlayerPosition(p3, 2, 1);
        map.setPlayerPosition(p4, 2, 3);
        tester.clear();
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));
    }

    /**
     * Tests the usage of the Flamethrower weapon, in both the modalities and in different situations
     *
     * @see Weapon#useEffect(Player, Effect, List, GameMap, List)
     */
    @Test
    public void testWeaponFlamethrower() {
        while (!(weaponTester.getName().equals("Flamethrower"))) weaponTester = decks.drawWeapon();

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 1, 1);
        map.setPlayerPosition(p4, 0, 2);
        tester.clear();
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 0);
        map.setPlayerPosition(p3, 1, 1);
        map.setPlayerPosition(p4, 1, 2);
        tester.clear();
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));
    }

    /**
     * Tests the usage of the Railgun weapon, in both the modalities and in different situations
     *
     * @see Weapon#useEffect(Player, Effect, List, GameMap, List)
     */
    @Test
    public void testWeaponRailgun() {
        while (!(weaponTester.getName().equals("Railgun"))) weaponTester = decks.drawWeapon();

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 1, 1);
        map.setPlayerPosition(p4, 0, 2);
        tester.clear();
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 1, 1);
        map.setPlayerPosition(p4, 0, 2);
        tester.clear();
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));
    }

    /**
     * Tests the usage of the ZX-2 weapon, in both the modalities and in different situations
     *
     * @see Weapon#useEffect(Player, Effect, List, GameMap, List)
     */
    @Test
    public void testWeaponZX2() {
        while (!(weaponTester.getName().equals("ZX-2"))) weaponTester = decks.drawWeapon();
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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 0);
        map.setPlayerPosition(p3, 2, 0);
        map.setPlayerPosition(p4, 1, 1);
        tester.clear();
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 0);
        map.setPlayerPosition(p3, 2, 0);
        map.setPlayerPosition(p4, 1, 1);
        tester.clear();
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));
    }

    /**
     * Tests the usage of the Shotgun weapon, in both the modalities and in different situations
     *
     * @see Weapon#useEffect(Player, Effect, List, GameMap, List)
     */
    @Test
    public void testWeaponShotgun() {
        while (!(weaponTester.getName().equals("Shotgun"))) weaponTester = decks.drawWeapon();

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 3);
        map.setPlayerPosition(p3, 0, 0);
        map.setPlayerPosition(p4, 0, 3);
        tester.clear();
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

        map.setPlayerPosition(p1, 1, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 0, 0);
        map.setPlayerPosition(p4, 1, 3);
        tester.clear();
        tester.add(new ArrayList<>());
        tester.get(0).add(new Damage());
        tester.get(0).get(0).setDamage(2);
        tester.get(0).get(0).setTarget(p2);
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 2, 3);
        map.setPlayerPosition(p3, 0, 0);
        map.setPlayerPosition(p4, 0, 3);
        tester.clear();
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));
    }

    /**
     * Tests the usage of the Power Glove weapon, in both the modalities and in different situations
     *
     * @see Weapon#useEffect(Player, Effect, List, GameMap, List)
     */
    @Test
    public void testWeaponPowerGlove() {
        while (!(weaponTester.getName().equals("Power Glove"))) weaponTester = decks.drawWeapon();

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

        map.setPlayerPosition(p1, 1, 1);
        map.setPlayerPosition(p2, 1, 1);
        map.setPlayerPosition(p3, 1, 2);
        map.setPlayerPosition(p4, 1, 0);
        tester.clear();
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));

        map.setPlayerPosition(p1, 1, 1);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 1, 1);
        map.setPlayerPosition(p4, 1, 0);
        tester.clear();
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));
    }

    /**
     * Tests the usage of the Shockwave weapon, in both the modalities and in different situations
     *
     * @see Weapon#useEffect(Player, Effect, List, GameMap, List)
     */
    @Test
    public void testWeaponShockwave() {
        while (!(weaponTester.getName().equals("Shockwave"))) weaponTester = decks.drawWeapon();

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

        map.setPlayerPosition(p1, 1, 1);
        map.setPlayerPosition(p2, 1, 1);
        map.setPlayerPosition(p3, 1, 0);
        map.setPlayerPosition(p4, 1, 2);
        tester.clear();
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));

        map.setPlayerPosition(p1, 1, 1);
        map.setPlayerPosition(p2, 1, 1);
        map.setPlayerPosition(p3, 1, 0);
        map.setPlayerPosition(p4, 1, 2);
        tester.clear();
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));
    }

    /**
     * Tests the usage of the Sledgehammer weapon, in both the modalities and in different situations
     *
     * @see Weapon#useEffect(Player, Effect, List, GameMap, List)
     */
    @Test
    public void testWeaponSledgehammer() {
        while (!(weaponTester.getName().equals("Sledgehammer"))) weaponTester = decks.drawWeapon();

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

        map.setPlayerPosition(p1, 1, 1);
        map.setPlayerPosition(p2, 0, 1);
        map.setPlayerPosition(p3, 1, 1);
        map.setPlayerPosition(p4, 2, 1);
        tester.clear();
        tester.add(new ArrayList<>());
        tester.get(0).add(new Damage());
        tester.get(0).get(0).setDamage(2);
        tester.get(0).get(0).setTarget(p3);
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 1, 1);
        map.setPlayerPosition(p3, 1, 0);
        map.setPlayerPosition(p4, 1, 2);
        tester.clear();
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));

        map.setPlayerPosition(p1, 1, 1);
        map.setPlayerPosition(p2, 0, 1);
        map.setPlayerPosition(p3, 1, 2);
        map.setPlayerPosition(p4, 2, 1);
        tester.clear();
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));
    }

    /**
     * Tests the usage of the Lock Rifle weapon and all of its effects
     *
     * @see Weapon#useEffect(Player, Effect, List, GameMap, List)
     */
    @Test
    public void testWeaponLockRifle() {
        while (!(weaponTester.getName().equals("Lock Rifle"))) weaponTester = decks.drawWeapon();

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

        map.setPlayerPosition(p1, 1, 3);
        map.setPlayerPosition(p2, 0, 0);
        map.setPlayerPosition(p3, 1, 0);
        map.setPlayerPosition(p4, 2, 0);
        tester.clear();
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

        map.setPlayerPosition(p1, 1, 3);
        map.setPlayerPosition(p2, 1, 3);
        map.setPlayerPosition(p3, 1, 3);
        map.setPlayerPosition(p4, 1, 3);
        ArrayList<Damage> forPotentiableWeapon = weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players).get(0);
        tester.clear();
        for (int i = 0; i < 2; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setMarks(1);
            tester.get(i).get(0).setTarget(i == 0 ? p3 : p4);
        }
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), forPotentiableWeapon, map, players));

        map.setPlayerPosition(p1, 1, 3);
        map.setPlayerPosition(p2, 1, 3);
        map.setPlayerPosition(p3, 0, 0);
        map.setPlayerPosition(p4, 0, 0);
        forPotentiableWeapon = weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players).get(0);
        tester.clear();
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), forPotentiableWeapon, map, players));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 1);
        map.setPlayerPosition(p3, 0, 2);
        map.setPlayerPosition(p4, 1, 3);
        forPotentiableWeapon = weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players).get(1);
        tester.clear();
        for (int i = 0; i < 2; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setMarks(1);
            tester.get(i).get(0).setTarget(i == 0 ? p2 : p4);
        }
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), forPotentiableWeapon, map, players));
    }

    /**
     * Tests the usage of the Machine Gun weapon and all of its effects
     *
     * @see Weapon#useEffect(Player, Effect, List, GameMap, List)
     */
    @Test
    public void testWeaponMachineGun() {
        while (!(weaponTester.getName().equals("Machine Gun"))) weaponTester = decks.drawWeapon();

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

        map.setPlayerPosition(p1, 1, 1);
        map.setPlayerPosition(p2, 0, 0);
        map.setPlayerPosition(p3, 0, 3);
        map.setPlayerPosition(p4, 2, 3);
        tester.clear();
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 1);
        map.setPlayerPosition(p3, 0, 2);
        map.setPlayerPosition(p4, 1, 3);
        ArrayList<Damage> forPotentiableWeapon = weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players).get(3);
        tester.clear();
        ArrayList<ArrayList<Damage>> forOrder = weaponTester.useEffect(p1, weaponTester.getEffects().get(1), forPotentiableWeapon, map, players);
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
        forPotentiableWeapon = weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players).get(3);
        forPotentiableWeapon.addAll(weaponTester.useEffect(p1, weaponTester.getEffects().get(1), forPotentiableWeapon, map, players).get(0));
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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(2), forPotentiableWeapon, map, players));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 1);
        map.setPlayerPosition(p3, 0, 2);
        map.setPlayerPosition(p4, 1, 3);
        forPotentiableWeapon = weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players).get(3);
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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(2), forPotentiableWeapon, map, players));
    }

    /**
     * Tests an exception when the Machine Gun weapon's optional effects are used in a wrong way
     * @see Weapon#useEffect(Player, Effect, List, GameMap, List)
     */
    @Test (expected = NullPointerException.class)
    public void testMachineGunNullPointerExceptionEffect1() {
        while (!(weaponTester.getName().equals("Machine Gun"))) weaponTester = decks.drawWeapon();
        weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players);
    }

    /**
     * Tests an exception when the Machine Gun weapon's optional effects are used in a wrong way
     * @see Weapon#useEffect(Player, Effect, List, GameMap, List)
     */
    @Test (expected = RuntimeException.class)
    public void testMachineGunRuntimeExceptionEffect1() {
        while (!(weaponTester.getName().equals("Machine Gun"))) weaponTester = decks.drawWeapon();
        ArrayList<Damage> forPotentiableWeapon = new ArrayList<>();
        weaponTester.useEffect(p1, weaponTester.getEffects().get(1), forPotentiableWeapon, map, players);
    }

    /**
     * Tests an exception when the Machine Gun weapon's optional effects are used in a wrong way
     * @see Weapon#useEffect(Player, Effect, List, GameMap, List)
     */
    @Test (expected = NullPointerException.class)
    public void testMachineGunNullPointerExceptionEffect2() {
        while (!(weaponTester.getName().equals("Machine Gun"))) weaponTester = decks.drawWeapon();
        weaponTester.useEffect(p1, weaponTester.getEffects().get(2), null, map, players);
    }

    /**
     * Tests an exception when the Machine Gun weapon's optional effects are used in a wrong way
     * @see Weapon#useEffect(Player, Effect, List, GameMap, List)
     */
    @Test (expected = RuntimeException.class)
    public void testMachineGunRuntimeExceptionEffect2() {
        while (!(weaponTester.getName().equals("Machine Gun"))) weaponTester = decks.drawWeapon();
        ArrayList<Damage> forPotentiableWeapon = new ArrayList<>();
        weaponTester.useEffect(p1, weaponTester.getEffects().get(2), forPotentiableWeapon, map, players);
    }

    /**
     * Tests the usage of the T.H.O.R. weapon and all of its effects
     *
     * @see Weapon#useEffect(Player, Effect, List, GameMap, List)
     */
    @Test
    public void testWeaponTHOR() {
        while (!(weaponTester.getName().equals("T.H.O.R."))) weaponTester = decks.drawWeapon();

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 1);
        map.setPlayerPosition(p3, 1, 0);
        map.setPlayerPosition(p4, 2, 1);
        ArrayList<ArrayList<Damage>> tester = new ArrayList<>();
        tester.add(new ArrayList<>());
        tester.get(0).add(new Damage());
        tester.get(0).get(0).setTarget(p2);
        tester.get(0).get(0).setDamage(2);
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 1);
        map.setPlayerPosition(p3, 1, 0);
        map.setPlayerPosition(p4, 2, 0);
        tester.clear();
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 1);
        map.setPlayerPosition(p3, 1, 0);
        map.setPlayerPosition(p4, 2, 1);
        tester.clear();
        ArrayList<Damage> forPotentiableWeapon = weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players).get(0);
        tester.add(new ArrayList<>());
        tester.get(0).add(new Damage());
        tester.get(0).get(0).setTarget(p3);
        tester.get(0).get(0).setDamage(1);
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), forPotentiableWeapon, map, players));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 1, 3);
        map.setPlayerPosition(p4, 2, 2);
        tester.clear();
        forPotentiableWeapon = weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players).get(0);
        for (int i = 0; i < 2; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(1);
        }
        tester.get(0).get(0).setTarget(p3);
        tester.get(1).get(0).setTarget(p4);
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), forPotentiableWeapon, map, players));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 0, 0);
        map.setPlayerPosition(p4, 0, 0);
        tester.clear();
        forPotentiableWeapon = weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players).get(0);
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), forPotentiableWeapon, map, players));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 1);
        map.setPlayerPosition(p3, 1, 0);
        map.setPlayerPosition(p4, 2, 1);
        tester.clear();
        forPotentiableWeapon = weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players).get(0);
        forPotentiableWeapon.addAll(weaponTester.useEffect(p1, weaponTester.getEffects().get(1), forPotentiableWeapon, map, players).get(0));
        tester.add(new ArrayList<>());
        tester.get(0).add(new Damage());
        tester.get(0).get(0).setTarget(p4);
        tester.get(0).get(0).setDamage(2);
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(2), forPotentiableWeapon, map, players));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 1, 3);
        map.setPlayerPosition(p4, 2, 2);
        tester.clear();
        forPotentiableWeapon = weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players).get(0);
        forPotentiableWeapon.addAll(weaponTester.useEffect(p1, weaponTester.getEffects().get(1), forPotentiableWeapon, map, players).get(0));
        for (int i = 0; i < 1; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(2);
        }
        tester.get(0).get(0).setTarget(p4);
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(2), forPotentiableWeapon, map, players));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 1, 3);
        map.setPlayerPosition(p4, 0, 0);
        tester.clear();
        forPotentiableWeapon = weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players).get(0);
        forPotentiableWeapon.addAll(weaponTester.useEffect(p1, weaponTester.getEffects().get(1), forPotentiableWeapon, map, players).get(0));
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(2), forPotentiableWeapon, map, players));
    }

    /**
     * Tests an exception when the T.H.O.R. weapon's optional effects are used in a wrong way
     * @see Weapon#useEffect(Player, Effect, List, GameMap, List)
     */
    @Test (expected = NullPointerException.class)
    public void testTHORNullPointerExceptionEffect1() {
        while (!(weaponTester.getName().equals("T.H.O.R."))) weaponTester = decks.drawWeapon();
        weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players);
    }

    /**
     * Tests an exception when the T.H.O.R. weapon's optional effects are used in a wrong way
     * @see Weapon#useEffect(Player, Effect, List, GameMap, List)
     */
    @Test (expected = RuntimeException.class)
    public void testTHORRuntimeExceptionEffect1() {
        while (!(weaponTester.getName().equals("T.H.O.R."))) weaponTester = decks.drawWeapon();
        ArrayList<Damage> forPotentiableWeapon = new ArrayList<>();
        weaponTester.useEffect(p1, weaponTester.getEffects().get(1), forPotentiableWeapon, map, players);
    }

    /**
     * Tests an exception when the T.H.O.R. weapon's optional effects are used in a wrong way
     * @see Weapon#useEffect(Player, Effect, List, GameMap, List)
     */
    @Test (expected = NullPointerException.class)
    public void testTHORNullPointerExceptionEffect2() {
        while (!(weaponTester.getName().equals("T.H.O.R."))) weaponTester = decks.drawWeapon();
        weaponTester.useEffect(p1, weaponTester.getEffects().get(2), null, map, players);
    }

    /**
     * Tests an exception when the T.H.O.R. weapon's optional effects are used in a wrong way
     * @see Weapon#useEffect(Player, Effect, List, GameMap, List)
     */
    @Test (expected = RuntimeException.class)
    public void testTHORRuntimeExceptionEffect2() {
        while (!(weaponTester.getName().equals("T.H.O.R."))) weaponTester = decks.drawWeapon();
        ArrayList<Damage> forPotentiableWeapon = new ArrayList<>();
        weaponTester.useEffect(p1, weaponTester.getEffects().get(2), forPotentiableWeapon, map, players);
    }

    /**
     * Tests the usage of the Plasma Gun weapon and all of its effects
     *
     * @see Weapon#useEffect(Player, Effect, List, GameMap, List)
     */
    @Test
    public void testWeaponPlasmaGun() {
        while (!(weaponTester.getName().equals("Plasma Gun"))) weaponTester = decks.drawWeapon();

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 1);
        map.setPlayerPosition(p3, 1, 0);
        map.setPlayerPosition(p4, 2, 1);
        ArrayList<ArrayList<Damage>> tester = new ArrayList<>();
        tester.add(new ArrayList<>());
        tester.get(0).add(new Damage());
        tester.get(0).get(0).setTarget(p2);
        tester.get(0).get(0).setDamage(2);
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 1);
        map.setPlayerPosition(p3, 1, 0);
        map.setPlayerPosition(p4, 2, 0);
        tester.clear();
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

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
        tester.get(0).get(0).setPosition(map.getCell(0, 0));
        tester.get(1).get(0).setPosition(map.getCell(0, 1));
        tester.get(2).get(0).setPosition(map.getCell(0, 2));
        tester.get(3).get(0).setPosition(map.getCell(2, 0));
        tester.get(4).get(0).setPosition(map.getCell(2, 1));
        tester.get(5).get(0).setPosition(map.getCell(2, 2));
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));

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
        tester.get(0).get(0).setPosition(map.getCell(0, 1));
        tester.get(1).get(0).setPosition(map.getCell(0, 2));
        tester.get(2).get(0).setPosition(map.getCell(0, 3));
        tester.get(3).get(0).setPosition(map.getCell(1, 3));
        tester.get(4).get(0).setPosition(map.getCell(2, 1));
        tester.get(5).get(0).setPosition(map.getCell(2, 2));
        tester.get(6).get(0).setPosition(map.getCell(2, 3));
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));

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
        tester.get(0).get(0).setPosition(map.getCell(0, 1));
        tester.get(1).get(0).setPosition(map.getCell(0, 2));
        tester.get(2).get(0).setPosition(map.getCell(1, 0));
        tester.get(3).get(0).setPosition(map.getCell(1, 1));
        tester.get(4).get(0).setPosition(map.getCell(2, 0));
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));

        map.setPlayerPosition(p1, 0, 3);
        map.setPlayerPosition(p2, 0, 1);
        map.setPlayerPosition(p3, 1, 0);
        map.setPlayerPosition(p4, 2, 1);
        tester.clear();
        ArrayList<Damage> forPotentiableWeapon = weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players).get(0);
        tester.add(new ArrayList<>());
        tester.get(0).add(new Damage());
        tester.get(0).get(0).setTarget(p2);
        tester.get(0).get(0).setDamage(1);
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(2), forPotentiableWeapon, map, players));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 1, 3);
        map.setPlayerPosition(p4, 2, 2);
        tester.clear();
        forPotentiableWeapon = weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players).get(1);
        tester.add(new ArrayList<>());
        tester.get(0).add(new Damage());
        tester.get(0).get(0).setTarget(p3);
        tester.get(0).get(0).setDamage(1);
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(2), forPotentiableWeapon, map, players));
    }

    /**
     * Tests an exception when the Plasma Gun weapon's optional effects are used in a wrong way
     * @see Weapon#useEffect(Player, Effect, List, GameMap, List)
     */
    @Test (expected = NullPointerException.class)
    public void testPlasmaGunNullPointerException() {
        while (!(weaponTester.getName().equals("Plasma Gun"))) weaponTester = decks.drawWeapon();
        weaponTester.useEffect(p1, weaponTester.getEffects().get(2), null, map, players);
    }

    /**
     * Tests an exception when the Plasma Gun weapon's optional effects are used in a wrong way
     * @see Weapon#useEffect(Player, Effect, List, GameMap, List)
     */
    @Test (expected = RuntimeException.class)
    public void testPlasmaGunRuntimeException() {
        while (!(weaponTester.getName().equals("Plasma Gun"))) weaponTester = decks.drawWeapon();
        ArrayList<Damage> forPotentiableWeapon = new ArrayList<>();
        weaponTester.useEffect(p1, weaponTester.getEffects().get(2), forPotentiableWeapon, map, players);
    }

    /**
     * Tests the usage of the Vortex Cannon weapon and all of its effects
     *
     * @see Weapon#useEffect(Player, Effect, List, GameMap, List)
     */
    @Test
    public void testWeaponVortexCannon() {
        while (!(weaponTester.getName().equals("Vortex Cannon"))) weaponTester = decks.drawWeapon();

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

        map.setPlayerPosition(p1, 0, 0);
        map.setPlayerPosition(p2, 1, 3);
        map.setPlayerPosition(p3, 2, 1);
        map.setPlayerPosition(p4, 2, 2);
        tester.clear();
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 3);
        map.setPlayerPosition(p3, 2, 2);
        map.setPlayerPosition(p4, 1, 3);
        ArrayList<Damage> forPotentiableWeapon = weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players).get(0);
        tester.clear();
        for (int i = 0; i < 2; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(1);
            tester.get(i).get(0).setTarget((i == 0) ? p3 : p4);
        }
        tester.get(0).get(0).setPosition(map.getCell(1, 2));
        tester.get(1).get(0).setPosition(map.getCell(1, 2));
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), forPotentiableWeapon, map, players));
    }

    /**
     * Tests the usage of the Grenade Launcher weapon and all of its effects
     *
     * @see Weapon#useEffect(Player, Effect, List, GameMap, List)
     */
    @Test
    public void testWeaponGrenadeLauncher() {
        while (!(weaponTester.getName().equals("Grenade Launcher"))) weaponTester = decks.drawWeapon();

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 1);
        map.setPlayerPosition(p3, 1, 0);
        map.setPlayerPosition(p4, 0, 0);
        tester.clear();
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 1);
        map.setPlayerPosition(p3, 0, 2);
        map.setPlayerPosition(p4, 1, 0);
        tester.clear();
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));
    }

    /**
     * Tests the usage of the Rocket Launcher weapon and all of its effects, except the "with rocket jump" one, as it is the same as the "with phase glide" effect of the "Plasma Gun" weapon
     *
     * @see Weapon#useEffect(Player, Effect, List, GameMap, List)
     */
    @Test
    public void testWeaponRocketLauncher() {
        while (!(weaponTester.getName().equals("Rocket Launcher"))) weaponTester = decks.drawWeapon();

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 1);
        map.setPlayerPosition(p3, 1, 0);
        map.setPlayerPosition(p4, 0, 0);
        tester.clear();
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 1, 2);
        map.setPlayerPosition(p3, 1, 2);
        map.setPlayerPosition(p4, 1, 2);
        tester.clear();
        ArrayList<Damage> forPotentiableWeapon = weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players).get(1);
        tester.add(new ArrayList<>());
        for (int i = 0; i < 3; i++) {
            tester.get(0).add(new Damage());
            tester.get(0).get(i).setDamage(1);
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(0).get(1).setTarget(p3);
        tester.get(0).get(2).setTarget(p4);
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(2), forPotentiableWeapon, map, players));

        map.setPlayerPosition(p1, 1, 1);
        map.setPlayerPosition(p2, 0, 2);
        map.setPlayerPosition(p3, 0, 2);
        map.setPlayerPosition(p4, 0, 2);
        tester.clear();
        forPotentiableWeapon = weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players).get(0);
        tester.add(new ArrayList<>());
        for (int i = 0; i < 3; i++) {
            tester.get(0).add(new Damage());
            tester.get(0).get(i).setDamage(1);
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(0).get(1).setTarget(p3);
        tester.get(0).get(2).setTarget(p4);
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(2), forPotentiableWeapon, map, players));
    }

    /**
     * Tests an exception when the Rocket Launcher weapon's optional effects are used in a wrong way
     * @see Weapon#useEffect(Player, Effect, List, GameMap, List)
     */
    @Test (expected = NullPointerException.class)
    public void testRocketLauncherNullPointerException() {
        while (!(weaponTester.getName().equals("Rocket Launcher"))) weaponTester = decks.drawWeapon();
        weaponTester.useEffect(p1, weaponTester.getEffects().get(2), null, map, players);
    }

    /**
     * Tests an exception when the Rocket Launcher weapon's optional effects are used in a wrong way
     * @see Weapon#useEffect(Player, Effect, List, GameMap, List)
     */
    @Test (expected = RuntimeException.class)
    public void testRocketLauncherRuntimeException() {
        while (!(weaponTester.getName().equals("Rocket Launcher"))) weaponTester = decks.drawWeapon();
        ArrayList<Damage> forPotentiableWeapon = new ArrayList<>();
        weaponTester.useEffect(p1, weaponTester.getEffects().get(2), forPotentiableWeapon, map, players);
    }

    /**
     * Tests the usage of the Cyberblade weapon and all of its effects
     *
     * @see Weapon#useEffect(Player, Effect, List, GameMap, List)
     */
    @Test
    public void testWeaponCyberblade() {
        while (!(weaponTester.getName().equals("Cyberblade"))) weaponTester = decks.drawWeapon();

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 0, 0);
        map.setPlayerPosition(p3, 1, 1);
        map.setPlayerPosition(p4, 0, 1);
        tester.clear();
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));

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
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(1), null, map, players));

        map.setPlayerPosition(p1, 2, 3);
        map.setPlayerPosition(p2, 2, 3);
        map.setPlayerPosition(p3, 2, 3);
        map.setPlayerPosition(p4, 2, 3);
        tester.clear();
        ArrayList<Damage> forPotentiableWeapon = weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players).get(0);
        for (int i = 0; i < 2; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(2);
        }
        tester.get(0).get(0).setTarget(p3);
        tester.get(1).get(0).setTarget(p4);
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(2), forPotentiableWeapon, map, players));

        map.setPlayerPosition(p1, 0, 0);
        map.setPlayerPosition(p2, 0, 0);
        map.setPlayerPosition(p3, 0, 0);
        map.setPlayerPosition(p4, 0, 0);
        tester.clear();
        forPotentiableWeapon = weaponTester.useEffect(p1, weaponTester.getEffects().get(0), null, map, players).get(2);
        for (int i = 0; i < 2; i++) {
            tester.add(new ArrayList<>());
            tester.get(i).add(new Damage());
            tester.get(i).get(0).setDamage(2);
        }
        tester.get(0).get(0).setTarget(p2);
        tester.get(1).get(0).setTarget(p3);
        assertEquals(tester, weaponTester.useEffect(p1, weaponTester.getEffects().get(2), forPotentiableWeapon, map, players));
    }

    /**
     * Tests an exception when the Cyberblade weapon's optional effects are used in a wrong way
     * @see Weapon#useEffect(Player, Effect, List, GameMap, List)
     */
    @Test (expected = NullPointerException.class)
    public void testCyberbladeNullPointerException() {
        while (!(weaponTester.getName().equals("Cyberblade"))) weaponTester = decks.drawWeapon();
        weaponTester.useEffect(p1, weaponTester.getEffects().get(2), null, map, players);
    }

    /**
     * Tests an exception when the Cyberblade weapon's optional effects are used in a wrong way
     * @see Weapon#useEffect(Player, Effect, List, GameMap, List)
     */
    @Test (expected = RuntimeException.class)
    public void testCyberbladeRuntimeException() {
        while (!(weaponTester.getName().equals("Cyberblade"))) weaponTester = decks.drawWeapon();
        ArrayList<Damage> forPotentiableWeapon = new ArrayList<>();
        weaponTester.useEffect(p1, weaponTester.getEffects().get(2), forPotentiableWeapon, map, players);
    }

    /**
     * Tests an exception when effectsCombination method gets called on a weapon that isn't a Potentiable one
     * @see Weapon#effectsCombinations()
     */
    @Test (expected = RuntimeException.class)
    public void testEffectsCombinationRuntimeException() {
        while (!(weaponTester.getName().equals("Whisper"))) weaponTester = decks.drawWeapon();
        weaponTester.effectsCombinations();
    }

    /**
     * Tests the method that generates the possible effects combination runs properly when using the Lock Rifle weapon
     * @see PotentiableWeapon#effectsCombinations()
     */
    @Test
    public void testEffectsCombinationLockRifle() {
        while (!(weaponTester.getName().equals("Lock Rifle"))) weaponTester = decks.drawWeapon();
        ArrayList<ArrayList<Integer>> combinationsTester = new ArrayList<>();
        combinationsTester.add(new ArrayList<>());
        combinationsTester.get(0).add(1);
        combinationsTester.add(new ArrayList<>());
        combinationsTester.get(1).add(1);
        combinationsTester.get(1).add(2);
        assertEquals(weaponTester.effectsCombinations(), combinationsTester);
    }

    /**
     * Tests the method that generates the possible effects combination runs properly when using the Machine Gun weapon
     * @see PotentiableWeapon#effectsCombinations()
     */
    @Test
    public void testEffectsCombinationMachineGun() {
        ArrayList<ArrayList<Integer>> combinationsTester = new ArrayList<>();
        while (!(weaponTester.getName().equals("Machine Gun"))) weaponTester = decks.drawWeapon();
        for (int i = 0; i < 4; i++) {
            combinationsTester.add(new ArrayList<>());
            combinationsTester.get(i).add(1);
        }
        combinationsTester.get(1).add(2);
        combinationsTester.get(2).add(3);
        combinationsTester.get(3).add(2);
        combinationsTester.get(3).add(3);
        assertEquals(weaponTester.effectsCombinations(), combinationsTester);
    }

    /**
     * Tests the method that generates the possible effects combination runs properly when using the T.H.O.R. weapon
     * @see PotentiableWeapon#effectsCombinations()
     */
    @Test
    public void testEffectsCombinationTHOR() {
        ArrayList<ArrayList<Integer>> combinationsTester = new ArrayList<>();
        while (!(weaponTester.getName().equals("T.H.O.R."))) weaponTester = decks.drawWeapon();
        for(int i = 0; i < 3; i++) {
            combinationsTester.add(new ArrayList<>());
            combinationsTester.get(i).add(1);
        }
        combinationsTester.get(1).add(2);
        combinationsTester.get(2).add(2);
        combinationsTester.get(2).add(3);
        assertEquals(weaponTester.effectsCombinations(), combinationsTester);
    }

    /**
     * Tests the method that generates the possible effects combination runs properly when using the Plasma Gun weapon
     * @see PotentiableWeapon#effectsCombinations()
     */
    @Test
    public void testEffectsCombinationPlasmaGun() {
        ArrayList<ArrayList<Integer>> combinationsTester = new ArrayList<>();
        while (!(weaponTester.getName().equals("Plasma Gun"))) weaponTester = decks.drawWeapon();
        for(int i = 0; i < 7; i++) {
            combinationsTester.add(new ArrayList<>());
            if(i < 4) combinationsTester.get(i).add(1);
            else combinationsTester.get(i).add(2);
        }
        combinationsTester.get(1).add(2);
        combinationsTester.get(2).add(3);
        combinationsTester.get(3).add(2);
        combinationsTester.get(3).add(3);
        combinationsTester.get(4).add(1);
        combinationsTester.get(5).add(1);
        combinationsTester.get(5).add(3);
        combinationsTester.get(6).clear();
        combinationsTester.get(6).add(1);
        combinationsTester.get(6).add(3);
        combinationsTester.get(6).add(2);
        assertEquals(weaponTester.effectsCombinations(), combinationsTester);
    }

    /**
     * Tests the method that generates the possible effects combination runs properly when using the Vortex Cannon weapon
     * @see PotentiableWeapon#effectsCombinations()
     */
    @Test
    public void testEffectsCombinationVortexCannon() {
        ArrayList<ArrayList<Integer>> combinationsTester = new ArrayList<>();
        while (!(weaponTester.getName().equals("Vortex Cannon"))) weaponTester = decks.drawWeapon();
        for(int i = 0; i < 2; i++) {
            combinationsTester.add(new ArrayList<>());
            combinationsTester.get(i).add(1);
        }
        combinationsTester.get(1).add(2);
        assertEquals(weaponTester.effectsCombinations(), combinationsTester);
    }

    /**
     * Tests the method that generates the possible effects combination runs properly when using the Grenade Launcher weapon
     * @see PotentiableWeapon#effectsCombinations()
     */
    @Test
    public void testEffectsCombinationGrenadeLauncher() {
        ArrayList<ArrayList<Integer>> combinationsTester = new ArrayList<>();
        while (!(weaponTester.getName().equals("Grenade Launcher"))) weaponTester = decks.drawWeapon();
        for(int i = 0; i < 3; i++) {
            combinationsTester.add(new ArrayList<>());
            if(i < 2) combinationsTester.get(i).add(1);
            else combinationsTester.get(i).add(2);
        }
        combinationsTester.get(1).add(2);
        combinationsTester.get(2).add(1);
        assertEquals(weaponTester.effectsCombinations(), combinationsTester);
    }

    /**
     * Tests the method that generates the possible effects combination runs properly when using the Rocket Launcher weapon
     * @see PotentiableWeapon#effectsCombinations()
     */
    @Test
    public void testEffectsCombinationRocketLauncher() {
        ArrayList<ArrayList<Integer>> combinationsTester = new ArrayList<>();
        while (!(weaponTester.getName().equals("Rocket Launcher"))) weaponTester = decks.drawWeapon();
        for(int i = 0; i < 7; i++) {
            combinationsTester.add(new ArrayList<>());
            if(i < 4) combinationsTester.get(i).add(1);
            else combinationsTester.get(i).add(2);
        }
        combinationsTester.get(1).add(2);
        combinationsTester.get(2).add(3);
        combinationsTester.get(3).add(2);
        combinationsTester.get(3).add(3);
        combinationsTester.get(4).add(1);
        combinationsTester.get(5).add(1);
        combinationsTester.get(5).add(3);
        combinationsTester.get(6).clear();
        combinationsTester.get(6).add(1);
        combinationsTester.get(6).add(3);
        combinationsTester.get(6).add(2);
        assertEquals(weaponTester.effectsCombinations(), combinationsTester);
    }

    /**
     * Tests the method that generates the possible effects combination runs properly when using the Cyberblade weapon
     * @see PotentiableWeapon#effectsCombinations()
     */
    @Test
    public void testEffectsCombinationCyberblade() {
        ArrayList<ArrayList<Integer>> combinationsTester = new ArrayList<>();
        while (!(weaponTester.getName().equals("Cyberblade"))) weaponTester = decks.drawWeapon();
        for(int i = 0; i < 7; i++) {
            combinationsTester.add(new ArrayList<>());
            if(i < 4) combinationsTester.get(i).add(1);
            else combinationsTester.get(i).add(2);
        }
        combinationsTester.get(1).add(2);
        combinationsTester.get(2).add(3);
        combinationsTester.get(3).add(2);
        combinationsTester.get(3).add(3);
        combinationsTester.get(4).add(1);
        combinationsTester.get(5).add(1);
        combinationsTester.get(5).add(3);
        combinationsTester.get(6).clear();
        combinationsTester.get(6).add(1);
        combinationsTester.get(6).add(3);
        combinationsTester.get(6).add(2);
        assertEquals(weaponTester.effectsCombinations(), combinationsTester);
    }
}
