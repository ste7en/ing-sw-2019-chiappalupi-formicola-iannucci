package it.polimi.ingsw;

import java.util.ArrayList;

public class Cell {

    private ArrayList<Border> borders;
    private CellColor color;
    private boolean respawner;
    private AmmoTile ammoCard;

    public Cell (ArrayList<Border> borders, CellColor color, boolean respawner, AmmoTile ammoCard) {
        this.borders.addAll(borders);
        this.color=color;
        this.respawner=respawner;
        this.ammoCard=ammoCard;
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

}