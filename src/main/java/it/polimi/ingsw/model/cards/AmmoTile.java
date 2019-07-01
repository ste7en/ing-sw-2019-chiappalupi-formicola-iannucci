package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.utility.AmmoColor;

import java.util.ArrayList;

/**
 *
 * @author Daniele Chiappalupi
 */
public class AmmoTile {

    /**
     * Private string used for the String construction of the toString;
     */
    private static final String AMMO_CARD = "Ammo Card";
    private static final String POWERUP   = "powerup";
    private static final String AMMO      = "ammo";

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
        return new ArrayList<>(ammoColors);
    }

    public void setPowerup(boolean powerup) {this.powerup = powerup;}

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(AMMO_CARD).append(": ");
        if(powerup) s.append(POWERUP).append(", ");
        for(int i = 0; i < ammoColors.size(); i++) {
            s.append(ammoColors.get(i).toString()).append(" ").append(AMMO);
            if(i != ammoColors.size()-1) s.append(", ");
            else s.append(";\n");
        }
        return s.toString();
    }

}