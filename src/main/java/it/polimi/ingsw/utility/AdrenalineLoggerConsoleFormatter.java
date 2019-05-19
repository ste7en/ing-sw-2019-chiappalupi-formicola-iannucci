package it.polimi.ingsw.utility;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.logging.*;

/**
 * Text formatter used when logging to the console
 *
 * @author Stefano Formicola
 */
public class AdrenalineLoggerConsoleFormatter extends Formatter {

    /**
     * Private constants used when writing a log record to string
     */
    private static final String space = " ";
    private static final String dash = "-";
    private static final String newline = "\n";

    /**
     * Format the given log record and return the formatted string.
     * <p>
     * The resulting formatted String will normally include a
     * localized and formatted version of the LogRecord's message field.
     * It is recommended to use the {@link Formatter#formatMessage}
     * convenience method to localize and format the message field.
     *
     * @param record the log record to be formatted.
     * @return the formatted log record
     */
    @Override
    public String format(LogRecord record) {
        return ANSIFromRecordLevel(record.getLevel())
                .concat(formattedLevel(record))
                .concat(space)
                .concat(formattedDate(record))
                .concat(space)
                .concat(dash)
                .concat(space)
                .concat(record.getMessage())
                .concat(newline)
                ;
    }

    /**
     * Returns a formatted string based on the record level
     * @param record a log record
     * @return formatted string
     */
    private String formattedLevel(LogRecord record) {
        var level = record.getLevel().getName();
        return "[" + level + "]"+space.repeat(7-level.length());
    }

    /**
     * Returns a specific ANSI color code for each record level
     * @param level a record level
     * @return ANSI color code as String
     */
    private String ANSIFromRecordLevel(Level level) {
        switch (level.getName()) {
            case "CONFIG": return ANSI_COLORS.ANSI_BRIGHT_BLUE.value();
            case "FINE": return ANSI_COLORS.ANSI_GREEN.value();
            case "INFO": return ANSI_COLORS.ANSI_WHITE.value();
            case "SEVERE": return ANSI_COLORS.ANSI_RED.value();
            case "WARNING": return ANSI_COLORS.ANSI_YELLOW.value();
            default: return ANSI_COLORS.ANSI_RESET.value();
        }
    }

    /**
     * A simple string formatted date of the record
     * @param record log record
     * @return date as string
     */
    private String formattedDate(LogRecord record) {
        var date = Date.from(record.getInstant());
        return new SimpleDateFormat("H:mm:ss.SSS")
                .format(date);
    }

    /**
     * Color values for console text colors
     */
    @SuppressWarnings("unused")
    private enum ANSI_COLORS {
        /**
         * Constants
         */
        ANSI_RESET("\u001B[0m"),
        ANSI_BLACK("\u001B[30m"),
        ANSI_RED("\u001B[31m"),
        ANSI_GREEN("\u001B[32m"),
        ANSI_YELLOW("\u001B[33m"),
        ANSI_BLUE("\u001B[34m"),
        ANSI_PURPLE("\u001B[35m"),
        ANSI_CYAN("\u001B[36m"),
        ANSI_WHITE("\u001B[37m"),
        ANSI_BRIGHT_BLACK("\u001B[90m"),
        ANSI_BRIGHT_RED("\u001B[91m"),
        ANSI_BRIGHT_GREEN("\u001B[92m"),
        ANSI_BRIGHT_YELLOW("\u001B[93m"),
        ANSI_BRIGHT_BLUE("\u001B[94m"),
        ANSI_BRIGHT_PURPLE("\u001B[95m"),
        ANSI_BRIGHT_CYAN("\u001B[96m"),
        ANSI_BRIGHT_WHITE("\u001B[97m");

        /**
         * private value of each constant
         */
        private String value;

        /**
         * Constructor
         * @param s the ANSI color code
         */
        ANSI_COLORS(String s) { value = s; }

        /**
         * Getter for the ANSI color code
         * @return the ANSI value
         */
        String value() { return value; }
    }
}
