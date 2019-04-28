package it.polimi.ingsw.networking.utility;

/**
 * This exception is thrown if there's an attempt to create a connection
 * with an unexpected port number like the ones reserved (0-1024) or
 * the ones invalid over 16-bit (65535+)
 *
 * @author Stefano Formicola
 */
public class IllegalPortNumber extends IllegalArgumentException {
    /**
     * Basic constructor
     */
    public IllegalPortNumber() {
        super();
    }
}
