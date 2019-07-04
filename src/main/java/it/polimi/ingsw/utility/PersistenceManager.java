package it.polimi.ingsw.utility;

/**
 * Application Persistence Manager that will
 * create snapshots of the application state
 *
 * @author Stefano Formicola
 */
public class PersistenceManager {

    private static PersistenceManager _instance;

    private PersistenceManager() {}

    /**
     * Singleton getter
     * @return the PersistenceManager singleton instance
     */
    public static synchronized PersistenceManager getInstance() {
        if (_instance == null) _instance = new PersistenceManager();
        return _instance;
    }

    /**
     *
     * @param o
     */
    public synchronized void saveSnapshot(Object o) {

    }

}
