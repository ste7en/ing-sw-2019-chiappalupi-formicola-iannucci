package it.polimi.ingsw.model.board;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.utility.CellColor;
import it.polimi.ingsw.model.cards.AmmoTile;
import it.polimi.ingsw.model.utility.Border;
import it.polimi.ingsw.model.utility.Direction;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Elena Iannucci
 */

public class Cell implements Comparable<Cell> {

    private static final String ANSI_RESET = "\u001B[0m";

    private static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    private static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    private static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
    private static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    private static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    private static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";


    /**
     * borders' types of the cell
     */
    @JsonProperty("borders")
    private List<Border> borders;

    /**
     * Cell's color
     */
    private CellColor color;

    /**
     * Boolean that is true only if the cell contains a spawnPoint
     */
    @JsonProperty("respawner")
    private boolean respawner;

    /**
     * Ammo tile positioned on the cell; it is null if the cell contains spawnPoints
     */
    private AmmoTile ammoCard;

    /**
     * Position of the cell in the matrix.
     */
    private int row;
    private int column;

    /**
     * Default constructor: added to let Jackson deserialize the map.
     */
    public Cell() {}

    /**
     * N.B.: this constructor acts only for testing purposes, as all the cell of the game maps are deserialized from json through the default constructor;
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
     * Cell's ansi color getter (needed to print it in the CLI).
     * @return the ANSI string color of the cell.
     */
    public String getANSIColor() {
        switch (color) {
            case red:
                return ANSI_RED_BACKGROUND;
            case yellow:
                return ANSI_YELLOW_BACKGROUND;
            case white:
                return ANSI_WHITE_BACKGROUND;
            case blue:
                return ANSI_BLUE_BACKGROUND;
            case pink:
                return ANSI_PURPLE_BACKGROUND;
            case green:
                return ANSI_GREEN_BACKGROUND;
        }
        return ANSI_RESET;
    }

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

    /**
     * Row getter.
     * @return the row of the cell.
     */
    public int getRow() {
        return row;
    }

    /**
     * Column getter.
     * @return the column of the cell.
     */
    public int getColumn() {
        return column;
    }
}