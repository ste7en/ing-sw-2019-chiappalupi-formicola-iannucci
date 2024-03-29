package it.polimi.ingsw.networking;

import it.polimi.ingsw.networking.utility.Ping;
import it.polimi.ingsw.utility.AdrenalineLogger;
import it.polimi.ingsw.utility.Loggable;
import it.polimi.ingsw.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Abstract class that will be reimplemented in ClientSocket and ClientRMI.
 *
 * @author Daniele Chiappalupi
 * @author Stefano Formicola
 * @author Elena Iannucci
 */
public abstract class Client implements Loggable {

    /**
     * Instance attributes that describe the connection
     */
    protected String serverName;
    protected Integer connectionPort;
    protected View viewObserver;
    protected UUID gameID;
    protected int userID;
    protected Timer pingTimer;

    /**
     * Log strings
     */
    @SuppressWarnings("squid:S3008")
    protected static final String UNKNOWN_HOST          = "Can't find the hostname. Asking for user input...";
    protected static final String IO_EXC                = "An IOException has been thrown. ";
    private static final String EXECUTION_EXC           = "Execution exception during a timeout operation :: ";
    private static final String INTERRUPTED_EXC         = "Timeout operation on client interrupted.";
    protected static final String CONN_RETRY            = "Connection retrying...";
    protected static final String ON_SUCCESS            = "ClientSocket successfully connected to the server.";
    private static final String ASK_SERVER_DETAILS      = "Asking for server hostname and connection port.";
    protected static final String INFO                  = "Setting up connection...";
    protected static final String CONNECTION_REFUSED    = "Connection refused. Asking for user input...";
    private static final String OBS_REGISTERED          = "Observer successfully registered.";

    /**
     * Helper strings for the following method
     */
    private static final String ASK_HOST_NAME           = "Please, insert server's address or hostname: ";
    private static final String ASK_PORT_NUMBER         = "Please, insert server's port number: ";
    private static final String CONNECTION_INTERRUPT    = "\n\u001B[31mConnection interrupted with the server, please reconnect.\u001B[0m\n";

    /**
     * Class constructor
     *
     * @param host hostname (IP address)
     * @param port port number of the listening server
     */
    public Client(String host, Integer port) {
        this.serverName = host;
        this.connectionPort = port;
        this.pingTimer = new Timer(true);

        setupConnection();
    }

    /**
     * Method that sets up the connection.
     * To be reimplemented in the sub-classes.
     */
    protected abstract void setupConnection();

    public void registerObserver(View viewObserver) {
        this.viewObserver = viewObserver;
        AdrenalineLogger.config(OBS_REGISTERED);
    }

    protected void onPong() {
        try {
            pingTimer.cancel();
        } catch (IllegalStateException e) { }
        finally {
            this.pingTimer = new Timer(true);
            this.pingTimer.schedule(new TimerTask() {
                public void run() {
                    viewObserver.onFailure(CONNECTION_INTERRUPT);
                }
            }, new Date(System.currentTimeMillis()+Ping.getInstance().getPingInterval()));
        }
    }

    /**
     * When an UnknownHostException is thrown, the user is asked to
     * insert server details again.
     */
    protected void askForUserInput() {
        AdrenalineLogger.info(ASK_SERVER_DETAILS);
        var in = new BufferedReader(new InputStreamReader(System.in));
        @SuppressWarnings("squid:S106")
        var out = System.out;

        try {
            out.print(ASK_HOST_NAME);
            this.serverName = in.readLine();
            out.print(ASK_PORT_NUMBER);
            this.connectionPort = Integer.parseInt(in.readLine());
        } catch (IOException e) {
            logOnException(IO_EXC, e);
        }
    }

    /**
     * Subclasses will call this method to notify the server an operation's
     * timeout expired
     */
    protected abstract void notifyServerTimeoutExpired();

    /**
     * Called to notify both View and Controller that
     * an operation's timeout expired
     */
    private void timeoutHasExpired() {
        this.viewObserver.timeoutHasExpired();
        notifyServerTimeoutExpired();
        closeClientConnection();
    }

    /**
     * Timeout operation client-side
     * @param timeoutInSeconds timeout in seconds to wait before blocking an operation
     * @param task {@link Runnable} task to run and wait for
     */
    protected void timeoutOperation(int timeoutInSeconds, Runnable task) {
        var ex = Executors.newFixedThreadPool(3);
        var future = ex.submit(task);

        try {
            future.get(timeoutInSeconds, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            this.timeoutHasExpired();
        } catch (ExecutionException e) {
            logOnException(EXECUTION_EXC+e.getCause(), e);
            this.timeoutHasExpired();
        } catch (InterruptedException e) {
            logOnException(INTERRUPTED_EXC, e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Called when a timeout expires, this method will close any incoming
     * connection to the client.
     */
    protected void closeClientConnection() {
        System.exit(2);
    }

    /**
     * Abstract method implemented by subclasses and used to create a new user
     * logged to the server.
     */
    public abstract void createUser(String username);

    /**
     * Abstract method implemented by subclasses to make the user joining
     * the server's waiting room.
     * @param username username
     */
    public abstract void joinWaitingRoom(String username);

    /**
     * Abstract method implemented by subclasses when the number of skulls has been decided.
     * @param choice it's the number of skulls that have been chosen.
     */
    public abstract void didChooseSkulls(String choice);

    /**
     * Abstract method implemented by subclasses when a turn or an action of a player is starting.
     */
    public abstract void newAction();

    /**
     * Abstract method implemented by subclasses when a player has chosen his character between the ones available.
     * @param characterColor the chosen character
     */
    public abstract void choseCharacter(String characterColor);

    /**
     * Abstract method implemented by subclasses when the first player has chosen the configuration of the map.
     * @param configuration the chosen configuration
     */
    public abstract void choseGameMap(String configuration);

    /**
     * Abstract method implemented by subclasses to get two Power Up cards between which the player will have to choose a spawnpoint.
     */
    public abstract void askForPossibleSpawnPoints();

    /**
     * Abstract method implemented by subclasses when a player has chosen his spawn point.
     */
    public abstract void choseSpawnPoint(String spawnPoint, String otherPowerup);

    /**
     * Abstract method implemented by subclasses when a player wants to pick something.
     * @param powerupToSell it's the list of powerup that the player wants to sell (empty if the player does not want to sell any powerup).
     */
    public abstract void askPicks(List<String> powerupToSell);

    /**
     * Abstract method implemented by subclasses when a player has decided what does he want to grab.
     */
    public abstract void didChooseWhatToGrab(String pick);

    /**
     * Abstract method implemented by subclasses when a player has decided which powerup does he want to discard.
     * @param powerup it's the Powerup::toString of the powerup to discard.
     */
    public abstract void powerupGrabToDiscard(String powerup);

    /**
     * Abstract method implemented by subclasses when a player has decided which weapon does he want to discard.
     * @param weapon it's the Weapon::getName of the powerup to discard.
     */
    public abstract void weaponGrabToDiscard(String weapon);

    /**
     * Abstract method implemented by subclasses when a player wants to sell any of his powerup to afford the cost of the weapon.
     */
    public abstract void powerupSellingToGrabWeapon();

    /**
     * Abstract method asking the server for available moves
     */
    public abstract void getAvailableMoves();

    /**
     * When the client chooses where to move
     * @param movement the chosen movement
     */
    public abstract void move(String movement);

    /**
     * Abstract method implemented by subclasses when a player wants to shoot people.
     */
    public abstract void askWeapons();

    /**
     * Abstract method implemented by subclasses and used when a player wants to use a weapon.
     *
     * @param weaponSelected it's a String containing the weapon selected name.
     */
    public abstract void useWeapon(String weaponSelected);

    /**
     * Abstract method implemented by subclasses and used when a player has decided if he wants to pay with his powerups the ammo costs.
     *
     * @param weaponSelected it's a String containing the weapon selected name.
     * @param powerups it's a list containing the powerups selected by the player.
     */
    public abstract void useWeaponAfterPowerupAsking(String weaponSelected, List<String> powerups);

    /**
     * Abstract method implemented by subclasses and used when a player has chosen what damage does he want to do with his weapon.
     * @param weapon It's the weapon that is being used.
     * @param damage It's the damage that is being done.
     * @param indexOfEffect It's the index of the effect that is being used.
     * @param forPotentiableWeapon It's the forPotentiableWeapon boolean.
     */
    public abstract void makeDamage(String weapon, String damage, String indexOfEffect, String forPotentiableWeapon);

    /**
     * Abstract method implemented by subclasses and used when a player has chosen what mode does he want to use with his Selectable Weapon.
     * @param weapon it's the weapon that is being used.
     * @param effect it's the mode that has being chosen.
     */
    public abstract void useMode(String weapon, String effect);

    /**
     * Abstract method implemented by subclasses and used when a player has chosen what effects does he want to use with his Potentiable weapon.
     * @param weapon it's the weapon that is being used.
     * @param effectsToUse it's a list containing all of the indexes of the effects to use.
     */
    public abstract void useEffect(String weapon, List<String> effectsToUse);

    /**
     * Abstract method implemented by subclasses and used when a player wants to reload his weapons.
     */
    public abstract void askWeaponToReload();

    /**
     * Abstract method implemented by subclasses and used when a player has chosen what weapons does he want to reload.
     * @param weaponsToReload it's the list of weapons selected by the user to be reloaded.
     */
    public abstract void reloadWeapons(List<String> weaponsToReload);

    /**
     * Abstract method implemented by subclasses and called when a player wants to use his powerup to get the ammos when reloading.
     */
    public abstract void askForPowerupsToReload();

    /**
     * Abstract method implemented by subclasses and called when a player has decided which powerup does he want to sell to afford the reloading cost.
     *
     * @param powerups it's the list of powerups chosen by the player.
     */
    public abstract void sellPowerupToReload(List<String> powerups);

    /**
     * Abstract method implemented by subclasses and used when a player wants to use a powerup.
     */
    public abstract void askForUsablePowerups();

    /**
     * Abstract method implemented by subclasses and used when a player has selected the powerup he wants to use.
     * @param powerup it's the powerup that the player has selected.
     */
    public abstract void askPowerupDamages(String powerup);

    /**
     * Abstract method implemented by subclasses and used when a player has decided how does he wants to use his powerup.
     * @param powerup it's the powerup that the player has selected.
     * @param damage it's the way the player has decided to use the powerup.
     */
    public abstract void usePowerup(String powerup, String damage);

    /**
     * Abstract method implemented by subclasses and used when an action has been successfully done.
     */
    public abstract void afterAction();

    /**
     * Abstract method implemented by subclasses and used when a death occurs.
     */
    public abstract void checkDeaths();

    /**
     * Abstract method implemented by subclasses and used when a turn of a player is finished.
     */
    public abstract void turnEnded();

    /**
     * Abstract method implemented by subclasses and called when a player has chosen where does he want to spawn after a death.
     * @param powerupChosen it's the powerup that has been chosen.
     */
    public abstract void spawnAfterDeathChosen(String powerupChosen);

    /**
     * Abstract method implemented to ask if the player wants to move.
     */
    public abstract void canMoveBeforeShoot();

    /**
     * Abstract method implemented by subclasses called when the player has to move before the shoot.
     */
    public abstract void movesBeforeShoot(String movement);

    /**
     * Abstract method implemented by subclasses called when the player has decided the tagback powerup he wants to use.
     */
    public abstract void tagback(String tagback);

    public abstract void didNotUseTagback();

}
