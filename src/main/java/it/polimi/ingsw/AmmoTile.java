package it.polimi.ingsw;

import java.util.*;

public class AmmoTile {

    private final ArrayList<AmmoColor> ammos;
    private final Powerup powerup;

    public AmmoTile(ArrayList<AmmoColor> ammos, Powerup powerup) {
        this.ammos = ammos;
        this.powerup = powerup;
    }

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