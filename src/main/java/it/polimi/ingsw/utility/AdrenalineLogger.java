package it.polimi.ingsw.utility;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.*;
import java.util.logging.Level;

/**
 * A logger class useful for both deployment and production debugging.
 * Not all the logging levels may have been handled but only the main ones useful
 * to get infos, warnings, errors or debug details.
 *
 * @author Stefano Formicola
 */
public class AdrenalineLogger {
    /**
     * Name of the log file
     */
    private static final String LOGNAME = "Adrenaline.log";

    /**
     * Error string for logging
     */
    private static final String FILE_HANDLER_ERR = "[ERROR] Could't instantiate a FileHandler.";

    /**
     * Private logger
     */
    private static final Logger logger = getLogger();

    private AdrenalineLogger() {}

    /**
     * Logger factor method
     * @return a specific logger for the app
     */
    private static Logger getLogger() {
        if (logger != null) return logger;
        Logger logger = Logger.getLogger(LOGNAME);
        Optional<FileHandler> fH = getFileHandler();
        fH.ifPresent(logger::addHandler);
        return logger;
    }

    /**
     * Wrapper for a file handler to write the log into a file.
     * If an IOException is thrown it returns an empty optional value.
     * @return an optional file handler
     */
    private static Optional<FileHandler> getFileHandler() {
        Optional<FileHandler> fH = Optional.empty();
        try {
            fH = Optional.of(new FileHandler(LOGNAME));
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, FILE_HANDLER_ERR);
        }
        return fH;
    }

    /**
     * Log a CONFIG message.
     * @param s config message
     */
    public static void config(String s) { logger.config(s); }

    /**
     * Log an INFO message.
     * @param s info message
     */
    public static void info(String s) { logger.info(s); }

    /**
     * Log a WARNING message.
     * @param s warning message
     */
    public static void warning(String s) { logger.warning(s); }

    /**
     * Log an ERROR message.
     * @param s error message
     */
    public static void error(String s) { logger.log(Level.SEVERE, s); }

    /**
     * Log a message, with associated Throwable information.
     * @param s message
     * @param t Throwable information
     */
    public static void errorException(String s, Throwable t) { logger.log(Level.SEVERE, s, t); }
}
