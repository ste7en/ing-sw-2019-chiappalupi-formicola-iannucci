package it.polimi.ingsw.model;

import java.util.ArrayList;

/**
 *
 * @author Daniele Chiappalupi
 */
public class AmmoTile {

    /**
     * Ammos provided by this AmmoTile
     */
    private final ArrayList<AmmoColor> ammos;

    /**
     * Boolean to save whether this AmmoTile provides a powerup when picked or not
     */
    private final boolean powerup;

    /**
     * Constructor: creates a new AmmoTile from an ArrayList of AmmoColors and a boolean
     * @param ammos ArrayList of AmmoColor that saves the list of ammos provided by the card
     * @param powerup boolean that shows if the card has a powerup
     */
    public AmmoTile(ArrayList<AmmoColor> ammos, boolean powerup) {
        this.ammos = ammos;
        this.powerup = powerup;
    }

    /**
     * Boolean that returns true if the AmmoTile provides a powerup when picked
     * @return the attribute {@link AmmoTile#powerup}
     */
    public Boolean hasPowerup() {
        return powerup;
    }

    /**
     * AmmoTile's AmmoColors getter
     * @return the list of ammos provided by the AmmoTile
     */
    public ArrayList<AmmoColor> getAmmoColors() {
        return (ArrayList<AmmoColor>)ammos.clone();
    }

}