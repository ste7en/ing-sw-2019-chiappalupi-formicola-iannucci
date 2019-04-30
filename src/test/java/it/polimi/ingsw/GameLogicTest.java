package it.polimi.ingsw;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class GameLogicTest {

    private static GameLogic gameLogicTest;


    /**
     * Initializes the attributes for the test class
     */
    @BeforeClass
    public static void setUpBeforeClass() {
    }

    /**
     * Initializes the GameMap before each test
     */
    @Before
    public void setUp() {
        gameLogicTest = new GameLogic();
    }

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

    }

    @Test
    public void testNumberCombination() {
        GameLogic g = new GameLogic();
        HashMap<Integer, ArrayList<Integer>> box = g.combinations(4, 2);
        System.out.print(box);
    }

}
