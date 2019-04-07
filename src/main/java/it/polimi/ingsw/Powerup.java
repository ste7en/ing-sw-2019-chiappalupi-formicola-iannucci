package it.polimi.ingsw;

/**
 *
 * @author Daniele Chiappalupi
 */
public class Powerup {

    /**
     * Type of the powerup
     */
    private final PowerupType type;

    /**
     * Description of the powerup
     */
    private final String description;

    /**
     * Color of the powerup
     */
    private final AmmoColor color;

    /**
     * Constructor: creates a new Powerup from its type, description and color
     * @param type type of the powerup
     * @param description description of the powerup
     * @param color color of the powerup, defined as an AmmoColor
     */
    public Powerup(PowerupType type, String description, AmmoColor color) {
        this.type = type;
        this.description = description;
        this.color = color;
    }

    /**
     * Powerup's type getter
     * @return the type of the powerup
     */
    public PowerupType getType() {
        return type;
    }

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