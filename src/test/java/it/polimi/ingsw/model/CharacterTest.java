package it.polimi.ingsw.model;

import it.polimi.ingsw.model.utility.PlayerColor;
import it.polimi.ingsw.model.player.Character;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test for {@link java.lang.Character} class
 *
 * @author Stefano Formicola
 */
public class CharacterTest {
    private Character character;
    private static PlayerColor colorTest;
    private static String characterName;
    private static String characterDescription;

    /**
     * Initializes the needed attributes for the test class
     */
    @BeforeClass
    public static void initializeCharacterTestClass() {
        colorTest = PlayerColor.yellow;
        characterName = "NameTest";
        characterDescription = "This is a medium length textual description of the character's role in the game.";
    }

    /**
     * Initializes the character before each test
     */
    @Before
    public void initializeCharacter() {
        character = new Character(characterName, colorTest, characterDescription);
    }

    /**
     * Tests the constructor of the {@link java.lang.Character} class
     * @see Character#Character(String, PlayerColor, String)
     */
    @Test
    public void testConstructor() {
        assertNotNull(character);
    }

    /**
     * Tests the getter method for character's name
     */
    @Test
    public void testGetName() {
        assertEquals(characterName, character.getName());
    }

    /**
     * Tests the getter method for character's color
     */
    @Test
    public void testGetColor() {
        assertEquals(colorTest, character.getColor());
    }

    /**
     * Tests the getter method for character's description
     */
    @Test
    public void testGetDescription() {
        assertEquals(characterDescription, character.getDescription());
    }

    /**
     * Tests the impossibility of creating a player with an empty string as name
     *@see Character#Character(String, PlayerColor, String)
     */
    @Test (expected = IllegalArgumentException.class)
    public void testConstructorWithEmptyName(){
        Character c = new Character("", colorTest, characterDescription);
    }

    /**
     * Tests the impossibility of creating a player with an empty string as description
     *@see Character#Character(String, PlayerColor, String)
     */
    @Test (expected = IllegalArgumentException.class)
    public void testConstructorWithEmptyDescription(){
        Character c = new Character(characterName, colorTest, "");
    }
}
