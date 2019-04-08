package it.polimi.ingsw;

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
        ammoTile = new AmmoTile(colors, true);
    }

    /**
     * Initializes the Cell before each test
     */
    @Before
    public void setUp(){
        cellTest = new Cell(Border.door, Border.space, Border.space, Border.wall, CellColor.yellow, false, ammoTile);

    }

    /**
     * Tests the adiacency method
     * @see Cell#adiacency(Direction)
     */
    @Test
    public void testAdiacency(){
        assertEquals(Border.door, cellTest.adiacency(Direction.North));
        assertEquals(Border.space, cellTest.adiacency(Direction.East));
        assertEquals(Border.space, cellTest.adiacency(Direction.South));
        assertEquals(Border.wall, cellTest.adiacency(Direction.West));
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
}

