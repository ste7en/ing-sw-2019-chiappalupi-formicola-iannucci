package it.polimi.ingsw.utility;

import java.util.logging.*;

/**
 * Simple plain text formatter for logging.
 *
 * @author Stefano Formicola
 */
public class AdrenalineLoggerFormatter extends Formatter {
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
        return formattedLevel(record) + formatMessage(record);
    }

    private String formattedLevel(LogRecord record) {
        String level = record.getLevel().getName();
        String formatted = "";
        switch (level) {
            case "CONFIG":
                formatted = ANSI_COLORS.ANSI_GREEN.value();
            case "INFO":
                formatted = ANSI_COLORS.ANSI_RESET.value();
            case "SEVERE":
                formatted = ANSI_COLORS.ANSI_RED.value();
            case "WARNING":
                formatted = ANSI_COLORS.ANSI_YELLOW.value();
        }
        formatted = formatted + "[" + level + "]";
        return formatted;
    }

    /**
     * Color values for console text colors
     */
    private enum ANSI_COLORS {
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

        private String value;

        // Constructor
        ANSI_COLORS(String s) { value = s; }

        String value() { return value; }
    }
}
