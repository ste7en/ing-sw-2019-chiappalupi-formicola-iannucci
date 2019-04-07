package it.polimi.ingsw;

import org.junit.*;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Test for {@link Cell} class
 *
 * @author Elena Iannucci
 */

public class CellTest {

    private Cell cellTest;
    private static AmmoTile ammoTile;
    private static ArrayList<Border> borders = new ArrayList<>();
    private static ArrayList<AmmoColor> colors = new ArrayList<>();

    /**
     * Initializes the attributes for the test class
     */
    @BeforeClass
    public static void setUpBeforeClass(){
        colors.add(AmmoColor.red);
        colors.add(AmmoColor.blue);
        ammoTile = new AmmoTile(colors, true);
        borders.add(Border.door);
        borders.add(Border.space);
        borders.add(Border.space);
        borders.add(Border.wall);
    }

    /**
     * Initializes the Cell before each test
     */
    @Before
    public void setUp(){
        cellTest = new Cell(borders, CellColor.yellow, false, ammoTile);

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

