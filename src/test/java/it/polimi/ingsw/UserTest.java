package it.polimi.ingsw;

import org.junit.*;
import java.util.UUID;
import static org.junit.Assert.*;

/**
 * Test for {@link User} class
 *
 * @author Daniele Chiappalupi
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
     * Tests the getUserID() method, proving both its type and the fact that two users don't get the same one
     * @see User#getUserID()
     */
    @Test
    public void testGetUserID() {
        assertNotNull(userTester1.getUserID());
        assertEquals(UUID.class, userTester1.getUserID().getClass());
        assertNotEquals(userTester1.getUserID(), userTester2.getUserID());
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
