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
     * String colours
     */
    private static final String ANSI_GREEN      = "\u001b[32m";
    private static final String ANSI_YELLOW     = "\u001b[33m";
    private static final String ANSI_BLUE       = "\u001b[34m";
    private static final String ANSI_MAGENTA    = "\u001b[35m";
    private static final String ANSI_WHITE      = "\u001b[37m";
    private static final String ANSI_RESET      = "\u001B[0m";

    /**
     * Possible names
     */
    private static final String D_STRUCT_OR = "D-Struct-Or";
    private static final String SPROG = "Sprog";
    private static final String VIOLET = "Violet";
    private static final String DOZER = "Dozer";
    private static final String BANSHEE = "Banshee";

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
     * Returns a String containing the coloured name of the character
     * @return the coloured name of the character
     */
    public String getColouredName() {
        StringBuilder nameBuilder = new StringBuilder();
        switch (color) {
            case green:
                nameBuilder.append(ANSI_GREEN);
                break;
            case blue:
                nameBuilder.append(ANSI_BLUE);
                break;
            case yellow:
                nameBuilder.append(ANSI_YELLOW);
                break;
            case grey:
                nameBuilder.append(ANSI_WHITE);
                break;
            case purple:
                nameBuilder.append(ANSI_MAGENTA);
                break;
        }

        nameBuilder.append(name);
        nameBuilder.append(ANSI_RESET);

        return nameBuilder.toString();
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

    /**
     * Returns the color of a character from its toString
     * @param toString it's the String to analyze
     * @return the color of the player, null if no player of this kind exists
     */
    public static String getColorFromToString(String toString) {
        toString = toString.replaceAll("[^a-zA-Z\\-]", "").replaceAll("m", "");
        switch (toString) {
            case D_STRUCT_OR:
                return PlayerColor.yellow.toString();
            case BANSHEE:
                return PlayerColor.blue.toString();
            case DOZER:
                return PlayerColor.grey.toString();
            case VIOLET:
                return PlayerColor.purple.toString();
            case SPROG:
                return PlayerColor.green.toString();
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        String coloredName = getColouredName();
        coloredName = coloredName.replace("\n", "").replace("\r", "");
        return coloredName;
    }

}