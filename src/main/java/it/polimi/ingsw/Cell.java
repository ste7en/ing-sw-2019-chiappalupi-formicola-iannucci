package it.polimi.ingsw;

import java.util.ArrayList;

public class Cell {

    private ArrayList<Border> borders;
    private CellColor color;
    private boolean respawner;
    private AmmoTile ammoCard;

    public Border adiacency(Direction direction) { return null; }

    public CellColor getColor() { return color; }

    public boolean isRespawn() { return respawner; }

    public AmmoTile getAmmoCard() { return ammoCard; }

}