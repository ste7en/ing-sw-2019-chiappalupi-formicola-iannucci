package it.polimi.ingsw.networking.utility;

/**
 * Thrown when a client attempts to use {@code null} in a
 * case where a view instance is required.
 *
 * @author Stefano Formicola
 */
public class ViewNotAssignedException extends NullPointerException {

    public static final String EXC_MESSAGE = "View instance not assigned to the client.";

    /**
     * Constructs a {@code NullPointerException} with no detail message.
     */
    public ViewNotAssignedException() {
        super(EXC_MESSAGE);
    }
}
