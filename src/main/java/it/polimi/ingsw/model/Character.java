package it.polimi.ingsw.model;

/**
 *
 * @author Stefano Formicola
 */
public class Character {

    /**
     * String passed as message of IllegalArgumentException when an empty string
     * is passed as name parameter of Character's constructor
     */
    private static final String EMPTY_STRING_NAME = "Cannot create a character with an empty string as name.";

    /**
     * String passed as message of IllegalArgumentException when an empty string
     * is passed as description parameter of Character's constructor
     */
    private static final String EMPTY_STRING_DESCRIPTION = "Cannot create a character with an empty string as description.";


    /**
     * Color of the character
     */
    private PlayerColor color;

    /**
     * Description of the character
     */
    private String description;

    /**
     * Character's name
     */
    private String name;

    /**
     * Constructor: creates a new Character based on its name, color and description.
     * @param name the name of the character
     * @param color the color of the character, defined as a PlayerColor Enum value
     * @param description the description of the character, as defined in the game's manual
     * @throws IllegalArgumentException for empty strings
     */
    public Character(String name, PlayerColor color, String description) {
        if (name.isEmpty()) { throw new IllegalArgumentException(EMPTY_STRING_NAME); }
        if (description.isEmpty()) { throw new IllegalArgumentException(EMPTY_STRING_DESCRIPTION); }

        this.name = name;
        this.color = color;
        this.description = description;
    }

    /**
     * Returns the name of the character
     * @return the name of the character
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the color of the character
     * @return the color of the character
     */
    public PlayerColor getColor() {
        return this.color;
    }

    /**
     * Returns the description of the character
     * @return the description of the character
     */
    public String getDescription() {
        return this.description;
    }

}