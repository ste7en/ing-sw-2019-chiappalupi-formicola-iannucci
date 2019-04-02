package it.polimi.ingsw;

import java.util.*;

public class AmmoTile {

    private ArrayList<AmmoColor> ammos;
    private Powerup powerup;

    public Boolean hasPowerup() {
        return (powerup != null) ? true : false;
    }

    public Powerup getPowerup() {
        return powerup;
    }

    public ArrayList<AmmoColor> getAmmoColors() {
        return ammos;
    }

}