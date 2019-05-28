package it.polimi.ingsw.networking.utility;

import it.polimi.ingsw.utility.AdrenalineLogger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Class that manages the connectivity state of a connection.
 * When a client doesn't respond after EXECUTION_PERIOD ms,
 * the server-side connection is closed.
 *
 * @author Stefano Formicola
 */
public class Ping {

    /**
     * Log strings
     */
    private static final String RUNTIME_EXC_PING = "A runtime exception has been thrown during ping.";

    /**
     * Period between a scheduled pings
     */
    private static final long EXECUTION_PERIOD = 1;

    /**
     * Delay when executing a ping for the first time
     */
    private static final long EXECUTION_DELAY = 0;

    /**
     * TimeUnit
     */
    private static final TimeUnit timeUnit = TimeUnit.SECONDS;

    /**
     * Collection that manages pings over time
     */
    private ConcurrentMap<Pingable, Boolean> pingHandler = new ConcurrentHashMap<>();

    /**
     * Singleton instance
     */
    private static Ping shared = new Ping();

    /**
     * Private constructor responsible for scheduling the ping command
     */
    private Ping() {
        /*
         * This lambda iterates over the pingHandler collection and pings every connection,
         * setting each state in the collection as false, meaning that a PONG message hasn't
         * been received yet. When a PONG message is received, the convenience didPong(int)
         * method re-sets the state to true. If a PONG message hasn't been received after
         * EXECUTION_PERIOD ms, the connection is interrupted meaning the client
         * isn't connected anymore.
         */
        new ScheduledThreadPoolExecutor(1)
                .scheduleAtFixedRate(
                        () -> {
                            try {
                                var pH = Ping.getInstance().getPingHandler();
                                pH.forEach(
                                        (p, b) -> {
                                            if (!b) { remove(p); return; }
                                            p.ping();
                                            pH.replace(p, false);
                                        }
                                );
                            } catch (RuntimeException e) {
                                AdrenalineLogger.errorException(RUNTIME_EXC_PING, e);
                            }

                        },
                        EXECUTION_DELAY,
                        EXECUTION_PERIOD,
                        timeUnit
                );
    }

    /**
     * Getter of the Ping instance
     * @return the Ping singleton instance
     */
    public static Ping getInstance() {
        shared = shared == null ? new Ping() : shared;
        return shared;
    }

    /**
     * Convenience method used to add a new connection to the pingHandler collection
     * @param connection the connection that has to be added
     */
    public void addPing(Pingable connection) {
        AdrenalineLogger.info("Added to ping: " + connection.hashCode());
        getPingHandler().put(connection, true);
    }

    /**
     * Convenience method used to notify a PONG message
     * @param connectionID the hashCode of the connection
     */
    public void didPong(int connectionID) {
        getPingHandler().keySet().forEach( connection -> {
            if (connection.getConnectionHashCode() == connectionID) {
                getPingHandler().replace(connection, true);
            }
        });
    }

    /**
     * Convenience method used to remove and close a connection to the pingHandler collection
     * @param connection the connection that has to be closed
     */
    private void remove(Pingable connection) {
        getPingHandler().remove(connection);
        connection.closeConnection();
    }

    /**
     * Getter of the pingHandler collection
     * @return the pingHandler instance
     */
    private ConcurrentMap<Pingable, Boolean> getPingHandler() { return this.pingHandler; }
}
