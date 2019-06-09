package it.polimi.ingsw.model;

import it.polimi.ingsw.model.utility.PlayerColor;
import it.polimi.ingsw.model.player.Character;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test for {@link java.lang.Character} class
 *
 * @author Stefano Formicola
 */
public class CharacterTest {
    private static Character character;
    private static String characterName = "D-Struct-Or";

    /**
     * Initializes the character before each test
     */
    @BeforeClass
    public static void initializeCharacter() {
        character = Character.getCharacterFromName(characterName);
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
        assertNotNull(character.getColor());
    }

    /**
     * Tests the getter method for character's description
     */
    @Test
    public void testGetDescription() {
        assertNotNull(character.getDescription());
    }

    /**
     * Tests the characters initialization from json
     */
    @Test
    public void testGetCharacters() {
        var characters = Character.getCharacters();
        assertNotNull(characters);
        assertFalse(characters.isEmpty());
    }

    /**
     * Tests getCharacterFromName method
     */
    @Test
    public void testGetCharacterFromName() {
        assertEquals(character, Character.getCharacterFromName(characterName));
    }

    /**
     * Tests getCharacterFromName method
     */
    @Test
    public void testGetCharacterFromColor() {
        assertEquals(character, Character.getCharacterFromColor(character.getColor()));
    }
}
