package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.DecksHandler;
import it.polimi.ingsw.model.board.Cell;
import it.polimi.ingsw.model.cards.AmmoTile;
import it.polimi.ingsw.model.utility.AmmoColor;
import it.polimi.ingsw.model.utility.Border;
import it.polimi.ingsw.model.utility.CellColor;
import it.polimi.ingsw.model.utility.Direction;
import org.junit.*;
import static org.junit.Assert.*;
import java.util.ArrayList;

/**
 * Test for {@link Cell} class
 *
 * @author Elena Iannucci
 */

public class CellTest {

    private Cell cellTest;
    private static AmmoTile ammoTile;
    private static ArrayList<AmmoColor> colors = new ArrayList<>();

    /**
     * Initializes the attributes for the test class
     */
    @BeforeClass
    public static void setUpBeforeClass(){
        colors.add(AmmoColor.red);
        colors.add(AmmoColor.blue);
        DecksHandler dh = new DecksHandler();
        ammoTile = dh.drawAmmoTile();
    }

    /**
     * Initializes the Cell before each test
     */
    @Before
    public void setUp(){
        cellTest = new Cell(Border.door, Border.space, Border.space, Border.wall, CellColor.yellow, false, ammoTile, 0, 0);

    }

    /**
     * Tests the adiajency method
     * @see Cell#adiajency(Direction)
     */
    @Test
    public void testAdiacency(){
        assertEquals(Border.door, cellTest.adiajency(Direction.North));
        assertEquals(Border.space, cellTest.adiajency(Direction.East));
        assertEquals(Border.space, cellTest.adiajency(Direction.South));
        assertEquals(Border.wall, cellTest.adiajency(Direction.West));
    }

    /**
     * Tests the getColor method
     * @see Cell#getColor()
     */
    @Test
    public void testGetColor(){
        assertNotNull(cellTest.getColor());
        assertEquals(CellColor.yellow, cellTest.getColor());
    }

    /**
     * Tests the isRespawn method
     * @see Cell#isRespawn()
     */
    @Test
    public void testIsRespawn(){
        assertFalse(cellTest.isRespawn());
    }

    /**
     * Tests the getAmmoCard method
     * @see Cell#getAmmoCard()
     */
    @Test
    public void testGetAmmoCard(){
        assertNotNull(cellTest.getAmmoCard());
        assertEquals(ammoTile, cellTest.getAmmoCard());
    }

    @Test
    public void test1() {
        String s = cellTest.getAmmoCard().toString();
        s = s.replaceAll("Ammo Card: ", "").replaceAll(" ammo", "").replaceAll(";", "");
        System.out.println(cellTest.getAmmoCard().toString());
        System.out.println(s);
    }
}

