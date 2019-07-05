package it.polimi.ingsw.utility;

import it.polimi.ingsw.networking.Server;

import java.io.*;

/**
 * Application Persistence Manager that will
 * create snapshots of the application state
 *
 * @author Stefano Formicola
 */
public class PersistenceManager implements Loggable {

    private static final String OBJ_OUTPUT_STREAM_EXC = "An exception has occurred while trying to save application state: ";
    private static final String OBJ_INPUT_STREAM_EXC  = "An exception has occurred while trying to load application state: ";
    private static final String CLASS_NOT_FOUND_EXC   = "Exception while trying to load application state: Object class not found. ";
    private static final String SNAPSHOT_SAVED_SUCC   = "Application state saved successfully.";

    private static final String FILE_NAME             = "Adrenaline_saved_state.bin";

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
        try (var objectOutputStream = new ObjectOutputStream(getOutputStream())) {
            objectOutputStream.writeObject(o);
        } catch (IOException e) {
            logOnException(OBJ_OUTPUT_STREAM_EXC, e);
        }
        logOnSuccess(SNAPSHOT_SAVED_SUCC);
    }

    public synchronized Server loadServerSnapshot() {
        try (var objectInputStream = new ObjectInputStream(getInputStream())) {
            return (Server) objectInputStream.readObject();
        } catch (IOException e) {
            logOnException(OBJ_INPUT_STREAM_EXC, e);
        } catch (ClassNotFoundException e) {
            logOnException(CLASS_NOT_FOUND_EXC, e);
        }
        return null;
    }

    public boolean isSnapshotAvailable() { return new File(FILE_NAME).exists(); }

    private InputStream getInputStream() throws FileNotFoundException {
        return new FileInputStream(FILE_NAME);
    }

    private OutputStream getOutputStream() throws FileNotFoundException {
        return new FileOutputStream(FILE_NAME);
    }

}
