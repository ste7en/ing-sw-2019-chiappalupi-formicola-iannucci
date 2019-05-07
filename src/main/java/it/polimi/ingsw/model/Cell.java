package it.polimi.ingsw;

import java.util.ArrayList;

/**
 *
 * @author Elena Iannucci
 */

public class Cell implements Comparable<Cell> {

    private ArrayList<Border> borders;
    private CellColor color;
    private boolean respawner;
    private AmmoTile ammoCard;

    //added for weapons testing purposes
    private final int row;
    private final int column;



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

    public Border adiacency(Direction direction) {
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

    public CellColor getColor() { return color; }

    public boolean isRespawn() { return respawner; }

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