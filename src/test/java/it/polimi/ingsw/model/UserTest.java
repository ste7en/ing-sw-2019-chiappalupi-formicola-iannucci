package it.polimi.ingsw.model;

import it.polimi.ingsw.model.player.User;
import org.junit.*;
import java.util.UUID;
import static org.junit.Assert.*;

/**
 * Test for {@link User} class
 *
 * @author Daniele Chiappalupi
 * @author Stefano Formicola
 */
public class UserTest {

    private User userTester1, userTester2;
    private static String username1, username2;

    /**
     * Initializes the username for the test class with a sample value
     */
    @BeforeClass
    public static void setUpBeforeClass() {
        username1 = "userTester1";
        username2 = "userTester2";
    }

    /**
     * Initializes the User before every test
     */
    @Before
    public void setUp() {
        userTester1 = new User(username1);
        userTester2 = new User(username2);
    }

    /**
     * Tests the equals(Object) method, proving two users may be equal if they have the same username
     * @see User#equals(Object)
     */
    @Test
    public void testEquality() {
        var user2 = new User(username1);
        @SuppressWarnings("")
        var user1Reference = userTester1;
        assertEquals(userTester1, user2);
        assertEquals(userTester1, user1Reference);
    }

    /**
     * Tests the hashCode() method, proving it returns username's hashcode
     * @see User#hashCode()
     */
    @Test
    public void testHashcode() {
        assertEquals(userTester1.hashCode(), username1.hashCode());
    }

    /**
     * Tests the getUsername() method, both with a positive and a negative test
     * @see User#getUsername()
     */
    @Test
    public void testGetUsername() {
        assertNotNull(userTester1.getUsername());
        assertEquals(username1, userTester1.getUsername());
        assertNotEquals(userTester1.getUsername(), userTester2.getUsername());
    }
}
