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
    private static final long EXECUTION_PERIOD = 500;

    /**
     * Delay when executing a ping for the first time
     */
    private static final long EXECUTION_DELAY = (long)500;

    /**
     * Maximum number of delayed pings
     */
    private static final int MAX_PING_TTL = 3;

    /**
     * TimeUnit
     */
    private static final TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    /**
     * Collection that manages pings over time
     */
    private ConcurrentMap<Pingable, Integer> pingHandler = new ConcurrentHashMap<>();

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
         * incrementing each counter in the collection, meaning that a PONG message can be
         * delayed. When a PONG message is received, the convenience didPong(int)
         * method re-sets the counter to 0. If a PONG message hasn't been received after
         * MAX_PING_TTL pings, the connection is interrupted meaning the client
         * isn't connected anymore.
         */
        new ScheduledThreadPoolExecutor(1)
                .scheduleAtFixedRate(
                        () -> {
                            try {
                                var pH = Ping.getInstance().getPingHandler();
                                pH.forEach(
                                        (p, n) -> {
                                            if (n >= MAX_PING_TTL) { remove(p); return; }
                                            pH.replace(p, n+1);
                                            p.ping();
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
        getPingHandler().put(connection, 0);
    }

    /**
     * Convenience method used to notify a PONG message
     * @param connectionID the hashCode of the connection
     */
    public void didPong(int connectionID) {
        getPingHandler().keySet().forEach( connection -> {
            if (connection.getConnectionHashCode() == connectionID) {
                getPingHandler().replace(connection, 0);
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
    private ConcurrentMap<Pingable, Integer> getPingHandler() { return this.pingHandler; }
}
