package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.utility.CellColor;
import it.polimi.ingsw.model.cards.AmmoTile;
import it.polimi.ingsw.model.utility.Border;
import it.polimi.ingsw.model.utility.Direction;

import java.util.ArrayList;

/**
 *
 * @author Elena Iannucci
 */

public class Cell implements Comparable<Cell> {

    /**
     * borders' types of the cell
     */
    private ArrayList<Border> borders;

    /**
     * Cell's color
     */
    private CellColor color;

    /**
     * Boolean that is true only if the cell contains a spawnpoint
     */
    private boolean respawner;

    /**
     * Ammo tile positioned on the cell; it is null if the cell contains spawnpoints
     */
    private AmmoTile ammoCard;

    //added for weapons testing purposes
    private final int row;
    private final int column;


    /**
     * Constructor: creates a new Cell based on its borders, color, ammo tile, position on the map and whether it is a respawner cell
     * @param b1 northern border of the cell, defined as a Border Enum value
     * @param b2 eastern border of the cell, defined as a Border Enum value
     * @param b3 southern border of the cell, defined as a Border Enum value
     * @param b4 western border of the cell, defined as a Border Enum value
     * @param color the color of the cell, defined as a CellColor Enum value
     * @param respawner boolean value that, if true, indicates that cell contains a spawnpoint
     * @param ammoCard ammo tile on the cell
     * @param row  the cell's row on the map
     * @param column the cell's column on the map
     */
    public Cell (Border b1, Border b2, Border b3, Border b4, CellColor color, boolean respawner, AmmoTile ammoCard, int row, int column) {
        borders = new ArrayList<>();
        this.borders.add(b1);
        this.borders.add(b2);
        this.borders.add(b3);
        this.borders.add(b4);
        this.color=color;
        this.respawner=respawner;
        this.ammoCard=ammoCard;
        this.row = row;
        this.column = column;
    }

    /**
     * Cell's border getter in a given direction
     * @param direction defined as a Direction Enum value
     * @return the border's type of the cell in the given direction
     */
    public Border adiajency(Direction direction) {
        switch(direction) {
            case North:
                return borders.get(0);
            case East:
                return borders.get(1);
            case South:
                return borders.get(2);
            case West:
                return borders.get(3);
        }
        return null;
    }

    /**
     * Cell's color getter
     * @return the color of the cell
     */
    public CellColor getColor() { return color; }

    /**
     * Method that shows whether the cell contains a spwanpoint
     * @return a boolean that is true if the cell contains a spawnpoint, false otherwise
     */
    public boolean isRespawn() { return respawner; }

    /**
     * Cell's ammo tile getter
     * @return the ammo tile positioned on the cell (null if the cell is a respawner cell)
     */
    public AmmoTile getAmmoCard() { return ammoCard; }

    @Override
    public int compareTo(Cell anotherCell) {
        return this.toString().compareToIgnoreCase(anotherCell.toString());
    }

    @Override
    public String toString() {
        return ("Row: " + row + "; Column: " + column);
    }

}