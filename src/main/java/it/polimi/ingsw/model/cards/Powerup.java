package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.utility.AmmoColor;

/**
 *
 * @author Daniele Chiappalupi
 */
public abstract class Powerup {

    /**
     * Description of the powerup
     */
    private String description;

    /**
     * Color of the powerup
     */
    private AmmoColor color;

    /**
     * Powerup's description getter
     * @return the description of the powerup
     */
    public String getDescription() {
        return description;
    }

    /**
     * Powerup's color getter
     * @return the color of the powerup
     */
    public AmmoColor getColor() {
        return color;
    }

}