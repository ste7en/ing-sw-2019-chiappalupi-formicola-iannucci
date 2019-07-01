package it.polimi.ingsw.utility;

/**
 * Basic interface useful for logging.
 *
 * @author Stefano Formicola
 */
public interface Loggable {
    /**
     * Log an operation ended with success
     * @param s message
     */
    default void logOnSuccess(String s) { AdrenalineLogger.success(s); }
    /**
     * Log an operation ended with failure
     * @param s message
     */
    default void logOnFailure(String s) { AdrenalineLogger.error(s); }
    /**
     * Log an operation ended with failure and an exception
     * @param s message
     */
    default void logOnException(String s, Throwable t) { AdrenalineLogger.errorException(s, t); }
    /**
     * Method to implement in order to debug with more verbosity a class or method
     */
    default void logDescription(Object o) { AdrenalineLogger.info(o.getClass().toGenericString()+": "+o.toString()); }
}
