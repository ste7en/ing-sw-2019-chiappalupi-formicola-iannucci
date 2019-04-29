package it.polimi.ingsw.utility;

import java.util.logging.*;

/**
 * A logger class useful for both deployment and production debugging.
 * Not all the logging levels may have been handled but only the main ones useful
 * to get infos, warnings, errors or debug details.
 *
 * @author Stefano Formicola
 */
public class AdrenalineLogger {
    private final static Logger logger = Logger.getLogger("Adrenaline.log");
}
