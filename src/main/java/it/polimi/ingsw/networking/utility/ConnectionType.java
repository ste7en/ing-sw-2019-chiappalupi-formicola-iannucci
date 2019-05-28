package it.polimi.ingsw.networking.utility;

/**
 * An helper enum used when handling two different connections.
 *
 * @author Stefano Formicola
 */
public enum ConnectionType {
    SOCKET,
    RMI;

    /**
     * Private string used when throwing an exception during a ConnectionType string parsing
     */
    private static String CONNECTION_TYPE_NOT_VALID = "Unable to parse the ConnectionType string: ";

    /**
     * Used when parsing user's command line input
     * when choosing which connection to use.
     *
     * @param s user's choice (rmi or socket)
     * @return a valid connection type or
     */
    public static ConnectionType parse(String s) {
        switch (s.toLowerCase().substring(0,3)) {
            case "soc":
                return SOCKET;
            case "rmi":
                return RMI;
            default: throw new IllegalArgumentException(CONNECTION_TYPE_NOT_VALID + s);
        }
    }
}
