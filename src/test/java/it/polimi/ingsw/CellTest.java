package it.polimi.ingsw;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;
import static junit.framework.TestCase.assertTrue;

public class CellTest {

    private Cell TestCell;
    private static ArrayList<AmmoTile> ammoTiles  = new ArrayList<>();
    private static ArrayList<Border> borders = new ArrayList<Border>();
    private static ArrayList<AmmoColor> colors;

    @BeforeClass
    public static void setUpBeforeClass(){
        Random rand = new Random();
        for (int i=0; i<4; i++){
            borders.add(Border.values()[new Random().nextInt(4)]);
        }
        CellColor color= CellColor.values()[new Random().nextInt(5)];

        ammoTiles.add(new AmmoTile(colors, new Random().nextBoolean() ? null : new Powerup("name", "description", AmmoColor.values()[new Random().nextInt(3)])));


    }

}
