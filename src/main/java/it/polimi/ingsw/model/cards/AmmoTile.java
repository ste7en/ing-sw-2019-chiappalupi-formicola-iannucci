package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.utility.AmmoColor;

import java.util.ArrayList;

/**
 *
 * @author Daniele Chiappalupi
 */
public class AmmoTile {

    /**
     * Ammos provided by this AmmoTile
     */
    private ArrayList<AmmoColor> ammoColors;

    /**
     * Boolean to save whether this AmmoTile provides a powerup when picked or not
     */
    private boolean powerup;

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
        return (ArrayList<AmmoColor>)ammoColors.clone();
    }

    public void setPowerup(boolean powerup) {this.powerup = powerup;}

}