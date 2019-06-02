package it.polimi.ingsw.networking;

import it.polimi.ingsw.model.player.User;
import it.polimi.ingsw.utility.AdrenalineLogger;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * The game Waiting Room, used to start a new game when the minimum number users
 * is available. It is invoked by the server and doesn't handle the game creation
 * but only a collection of Users.
 *
 * @author Stefano Formicola
 */
public class WaitingRoom {

    /**
     * Log strings
     */
    private static final String WAITING_ROOM_CREATED = "Waiting Room created";
    private static final String USER_JOINED          = "User joined the waiting room :: ";
    private static final String MIN_NUM_REACHED      = "Minimum number of users for a game reached";
    private static final String MAX_NUM_REACHED      = "Maximum number of users for a game reached. Next users will be added for a new game.";
    private static final String COUNTDOWN_STARTED    = "A new game is going to be created. Countdown started...";
    private static final String NEXT_GAME_CREATION   = "Next game will be created in ";

    /**
     * Exception strings
     */
    private static final String USER_ALREADY_ADDED_EXC = "User already added :: ";
    /**
     * The minimum number of users needed to create a game
     */
    private final int minimumNumberOfPlayers;

    /**
     * The maximum number of users needed to create a game
     */
    private final int maximumNumberOfPlayers;

    /**
     * The time to wait before notifying the observer
     */
    private final long timeout;

    /**
     * The observer Server, notified when a new game can be started
     */
    private final WaitingRoomObserver observer;

    /**
     * The concurrent collection of users in queue, waiting to start a new game
     */
    private final ConcurrentLinkedQueue<User> userQueue;

    /**
     * A helper collection of users needed to create one game
     * at time with max maximumNumberOfPlayers players
     */
    private final LinkedList<User> userWaitingList;

    /**
     * Boolean variable, true when the countdown has started,
     * otherwise it is false.
     */
    private boolean isRunning = false;


    /**
     * WaitingRoom constructor
     * @param minimumNumberOfPlayers minimum number of users needed to create a game
     * @param maximumNumberOfPlayers maximum number of users needed to create a game
     * @param timeout  time to wait before notifying the observer
     * @param waitingRoomObserver observer Server, notified when a new game can be started
     */
    WaitingRoom(int minimumNumberOfPlayers, int maximumNumberOfPlayers, long timeout, WaitingRoomObserver waitingRoomObserver) {
        this.minimumNumberOfPlayers = minimumNumberOfPlayers;
        this.maximumNumberOfPlayers = maximumNumberOfPlayers;
        this.timeout                = timeout;
        this.observer               = waitingRoomObserver;

        userQueue = new ConcurrentLinkedQueue<>();
        userWaitingList = new LinkedList<>();

        AdrenalineLogger.config(WAITING_ROOM_CREATED);
    }

    /**
     * Adds users to the waiting room
     * @param user User instance
     * @return true if the minimum number of users has reached
     */
    @SuppressWarnings("squid:S00112")
    public boolean addUser(User user) {
        if (userQueue.contains(user) || userWaitingList.contains(user)) throw new RuntimeException(USER_ALREADY_ADDED_EXC + user.toString());
        if (userQueue.size() < maximumNumberOfPlayers) {
            userQueue.add(user);
        } else {
            userWaitingList.add(user);
        }

        AdrenalineLogger.info(USER_JOINED + user.getUsername());
        if (userQueue.size() == maximumNumberOfPlayers) AdrenalineLogger.info(MAX_NUM_REACHED);

        return didAddUser();
    }

    /**
     * Flushes the concurrent collection of users and
     * transfers the users in queue for a new game.
     */
    private void flushQueue() {
        userQueue.clear();
        isRunning = false;
        while(userQueue.size() < maximumNumberOfPlayers || !userWaitingList.isEmpty()) {
            addUser(userWaitingList.pop());
        }
    }

    /**
     * Called when a new user is added to the waiting room
     * @return true if a minimum number has been reached, false otherwise
     */
    private boolean didAddUser() {
        if (userQueue.size() >= minimumNumberOfPlayers) {
            if (!isRunning) {
                AdrenalineLogger.success(MIN_NUM_REACHED);
                didReachMinimumNumberOfPlayers();
            }
            return true;
        }
        return false;
    }

    /**
     * Called when a minimum number of connected users is reached,
     * this methods calls the server's method responsible for
     * creating a new game with the selected users.
     */
    private void didReachMinimumNumberOfPlayers() {
        AdrenalineLogger.info(COUNTDOWN_STARTED);
        AdrenalineLogger.success(NEXT_GAME_CREATION + timeout + "ms");
        isRunning = true;
        Executors.newSingleThreadScheduledExecutor()
                 .schedule(
                         () -> {
                             observer.startNewGame(Arrays.asList(userQueue.toArray(new User[0])));
                             flushQueue();
                            },
                         timeout,
                         TimeUnit.MILLISECONDS
                 );
    }
}
