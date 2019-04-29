package it.polimi.ingsw.utility;

/**
 * Basic interface useful for logging.
 *
 * @author Stefano Formicola
 */
public interface Loggable {
    default void logOnSuccess(String s) { AdrenalineLogger.info(s); }
    default void logOnFailure(String s) { AdrenalineLogger.error(s); }
    default void logOnException(String s, Throwable t) { AdrenalineLogger.errorException(s, t); }
    void logDescription();
}
