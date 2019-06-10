package it.polimi.ingsw.model.player;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.model.utility.PlayerColor;
import it.polimi.ingsw.utility.AdrenalineLogger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
     * String constant used in messages between client-server
     */
    public final static String character_list = "CHARACTER_LIST";
    public final static String character      = "CHARACTER";

    /**
     * Log string
     */
    private static final String ERR_CHARACTER_INIT = "An error has occurred while trying to read characters from json.";

    /**
     * Number of available characters for the game
     */
    private static final int NUM_CHARACTERS = 5;

    /**
     * Collection of private available characters
     */
    private static final ArrayList<Character> characters = initializeCharacters();

    /**
     * Character's name
     */
    private String name;
    /**
     * Color of the character
     */
    private PlayerColor color;

    /**
     * Description of the character
     */
    private String description;

    /**
     * Public initializer only for tests
     * @param name character name
     * @param color character color
     * @param description character description
     */
    public Character(String name, PlayerColor color, String description) {}

    /**
     * Default constructor for json parsing
     */
    private Character() {}

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

    /**
     * Method that initializes the weapons deck using a json file
     * @return an ArrayList containing the deck of weapons for testing purposes
     */
    private static ArrayList<Character> initializeCharacters() {
        ObjectMapper objectMapper = new ObjectMapper();
        Character[] box = new Character[NUM_CHARACTERS];
        try {
            File json = new File("src" + File.separator + "main" + File.separator + "resources" + File.separator + "characters.json");
            box = objectMapper.readValue(json, Character[].class);

        } catch (IOException e) {
            AdrenalineLogger.errorException(ERR_CHARACTER_INIT, e);
        }
        return new ArrayList<>(Arrays.asList(box));
    }

    /**
     * Getter of all the available characters
     * @return a collection of all the available characters of the game
     */
    public static List<Character> getCharacters() { return (ArrayList<Character>)characters.clone(); }

    /**
     * Returns a Character from a given name, if exists
     * @param name character name
     * @return a character
     */
    public static Character getCharacterFromName(String name) {
        return characters.stream().filter(character -> character.getName().equals(name)).findFirst().orElseThrow();
    }

    /**
     * Returns a Character from a given color, if exists
     * @param color character color
     * @return a character
     */
    public static Character getCharacterFromColor(PlayerColor color) {
        return characters.stream().filter(character -> character.getColor().equals(color)).findFirst().orElseThrow();
    }

}