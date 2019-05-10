package it.polimi.ingsw.utility;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link AdrenalineLogger} class
 * Those tests are not commented because they're self-explanatory
 *
 * @author Stefano Formicola
 */
public class AdrenalineLoggerTest {
    @BeforeClass
    public static void setup() { AdrenalineLogger.setLogName("TEST"); }

    @Test
    public void config() {
        AdrenalineLogger.config("This is a config message.");
    }

    @Test
    public void info() {
        AdrenalineLogger.info("This is an info message.");
    }

    @Test
    public void warning() {
        AdrenalineLogger.warning("This is a warning message.");
    }

    @Test
    public void error() {
        AdrenalineLogger.error("This is an error message.");
    }

    @Test
    public void errorException() {
        AdrenalineLogger.errorException("Exception message", new RuntimeException("Inner exception message."));
    }

    @Test
    public void success() { AdrenalineLogger.success("This message has to be shown when an operation ends successfully.");}
}