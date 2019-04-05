package it.polimi.ingsw;

/**
 *
 * @author Daniele Chiappalupi
 */
public class Powerup {

    /**
     * Name of the powerup
     */
    private final String name;

    /**
     * Description of the powerup
     */
    private final String description;

    /**
     * Color of the powerup
     */
    private final AmmoColor color;

    /**
     * Constructor: creates a new Powerup from its name, description and color
     * @param name name of the powerup
     * @param description description of the powerup
     * @param color color of the powerup, defined as an AmmoColor
     */
    public Powerup(String name, String description, AmmoColor color) {
        this.name = name;
        this.description = description;
        this.color = color;
    }

    /**
     * Powerup's name getter
     * @return the name of the powerup
     */
    public String getName() {
        return name;
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