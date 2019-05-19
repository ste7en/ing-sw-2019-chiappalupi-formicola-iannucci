package it.polimi.ingsw.networking.utility;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for the ConnectionType enum's parse(String) method
 *
 * @author Stefano Formicola
 */
public class ConnectionTypeTest {

    /**
     * Tests are self-explanatory
     */

    @Test
    public void testParsing() {
        assertEquals(ConnectionType.SOCKET, ConnectionType.parse("Socket"));
        assertEquals(ConnectionType.SOCKET, ConnectionType.parse("SOCKET"));
        assertEquals(ConnectionType.SOCKET, ConnectionType.parse("SOCK"));
        assertEquals(ConnectionType.SOCKET, ConnectionType.parse("SOC"));
        assertEquals(ConnectionType.SOCKET, ConnectionType.parse("SOCCHETT"));

        assertEquals(ConnectionType.RMI, ConnectionType.parse("Rmi"));
        assertEquals(ConnectionType.RMI, ConnectionType.parse("rmi"));
        assertEquals(ConnectionType.RMI, ConnectionType.parse("RMI"));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testException() {
        ConnectionType.parse("undefined connection");
    }
}