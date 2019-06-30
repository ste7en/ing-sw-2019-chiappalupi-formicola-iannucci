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
    private static final String APP_NAME = "Adrenaline";
    private static final String SEPARATOR = "_";
    private static final String EXTENSION = ".log";
    private static String LOG_TYPE = "LOG";

    /**
     * Error string for logging
     */
    private static final String FILE_HANDLER_ERR = "[ERROR] Could't instantiate a FileHandler.";

    /**
     * Private logger
     */
    private static Logger logger;

    /**
     * Setter to change the default log name basing on the user (client/server)
     */
    public static void setLogName(String n) {
        LOG_TYPE = n;
        logger = getLogger();
    }

    /**
     * @return a string used to save the log in a file
     */
    private static String getLogName() {
        return APP_NAME + SEPARATOR + LOG_TYPE + EXTENSION;
    }

    /**
     * Logger factor method
     * @return a specific logger for the app
     */
    private static Logger getLogger() {
        if (logger != null) return logger;
        logger = Logger.getLogger(APP_NAME);
        // Removing default console handler
        logger.setUseParentHandlers(false);
        // Adding a custom console handler
        logger.addHandler(getConsoleHandler());
        // Adding a custom file handler
        Optional<FileHandler> fH = getFileHandler();
        fH.ifPresent(logger::addHandler);

        logger.setLevel(Level.ALL);
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
            String logName = getLogName();
            fH = Optional.of(new FileHandler(logName));
            fH.ifPresent (fileHandler -> {
                fileHandler.setFormatter(new SimpleFormatter());
                fileHandler.setLevel(Level.ALL);
            });
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, FILE_HANDLER_ERR);
        }
        return fH;
    }

    /**
     * Wrapper for a console handler to write the log in the console.
     * If an IOException is thrown it returns an empty optional value.
     * @return an optional console handler
     */
    private static ConsoleHandler getConsoleHandler() {
        ConsoleHandler cH = new ConsoleHandler();
        cH.setFormatter(new AdrenalineLoggerConsoleFormatter());
        cH.setLevel(Level.ALL);
        return cH;
    }

    /**
     * Log a CONFIG message.
     * @param s config message
     */
    public static void config(String s) { getLogger().config(s); }

    /**
     * Log a FINE message used for operations ended with success.
     * @param s message
     */
    public static void success(String s) { getLogger().fine(s); }

    /**
     * Log an INFO message.
     * @param s info message
     */
    public static void info(String s) { getLogger().info(s); }

    /**
     * Log a WARNING message.
     * @param s warning message
     */
    public static void warning(String s) { getLogger().warning(s); }

    /**
     * Log an ERROR message.
     * @param s error message
     */
    public static void error(String s) { getLogger().log(Level.SEVERE, s); }

    /**
     * Log a message, with associated Throwable information.
     * @param s message
     * @param t Throwable information
     */
    public static void errorException(String s, Throwable t) { getLogger().log(Level.SEVERE, s, t); }
}
