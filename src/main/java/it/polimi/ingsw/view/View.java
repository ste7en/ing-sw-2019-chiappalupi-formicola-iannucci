package it.polimi.ingsw.view;

import it.polimi.ingsw.model.PlayerColor;
import it.polimi.ingsw.networking.Client;
import it.polimi.ingsw.networking.utility.ConnectionType;
import it.polimi.ingsw.utility.AdrenalineLogger;

import java.util.Observer;


/**
 * View class of the MVC paradigm.
 * It's abstract because several methods will be either overridden or implemented in its children classes.
 *
 * @author Daniele Chiappalupi, Stefano Formicola, Elena Iannucci
 */
public abstract class View implements Observer{

    private Client client;

    private PlayerColor playerColor;

    /**
     * Log strings
     */
    private static String APPLICATION_STARTED   = "Adrenaline application started. View instance created.";
    private static String DID_ASK_CONNECTION    = "Connection parameters chosen: ";
    private static String ONLOGIN               = "";
    private static String ONLOGIN_FAILURE       = "";
    private static String ONLOGIN_SUCCESS       = "";
    private static String JOIN_WAITING_ROOM     = "";
    private static String DID_JOIN_WAITING_ROOM = "";


    public abstract void onViewUpdate();

    public abstract void onFailure();

    public abstract void onStart();

    /**
     * Public method implemented by subclasses when starting a new application instance.
     * Both subclasses will implement a convenience method to let the user choose the connection.
     */
    private void willChooseConnection() {
        chooseConnection();
    }

    /**
     * Subclass method to be implemented, that will prompt
     * the user to choose connection parameters.
     */
    protected abstract void chooseConnection();

    /**
     * Method called after a user choice of the connection.
     * Shouldn't be reimplemented by a subclass.
     */
    protected void didChooseConnection(ConnectionType type, int port, String host) {
        AdrenalineLogger.info(DID_ASK_CONNECTION + type + " " + host + ":" + port);
        this.client = new Client(type, host, port);
        client.registerObserver(this);
    }

    /**
     * Ste
     */
    public abstract void login();

    /**
     * Ste
     */
    public abstract void onLoginFailure();

    /**
     * Ste
     */
    public abstract void onLoginSuccess();

    /**
     * Ste
     */
    public abstract void joinWaitingRoom();

    /**
     * Ste
     */
    public abstract void didJoinWaitingRoom();

    /**
     * Ele
     */
    public abstract void willChooseCharacter();

    /**
     * Ele
     */
    public abstract void didChooseCharacter();

    /**
     * Ele
     */
    public abstract void willChooseGameSettings();

    /**
     * Ele
     */
    public abstract void didChooseGameSettings();

    /**
     * Ele
     */
    public abstract void willChooseSpawnPoint();

    /**
     * Ele
     */
    public abstract void didChooseSpawnPoint();

    /**
     * Ele
     */
    public abstract void onMove();

    /**
     * Ele
     */
    public abstract void willChooseMovement();

    /**
     * Ele
     */
    public abstract void didChooseMovement();

    /**
     * Ele
     */
    public abstract void willChooseWhatToGrab();

    /**
     * Ele
     */
    public abstract void didChooseWhatToGrab();

    /**
     * Dani
     */
    public abstract void willChooseWeapon();

    /**
     * Dani
     */
    public abstract void didChooseWeapon();

    /**
     * Dani
     */
    public abstract void willChooseDamage();

    /**
     * Dani
     */
    public abstract void didChooseDamage();

    /**
     * Dani
     */
    public abstract void willChooseMode();

    /**
     * Dani
     */
    public abstract void didChooseMode();

    /**
     * Dani
     */
    public abstract void willChooseEffects();

    /**
     * Dani
     */
    public abstract void didChooseEffects();

    /**
     * Ste
     */
    public abstract void onEndTurn();

    /**
     * Dani
     */
    public abstract void willReload();

    /**
     * Dani
     */
    public abstract void didReload();

    /**
     * Ste ft. Ele
     */
    public abstract void willUsePowerup();

    /**
     * Ste ft. Ele
     */
    public abstract void didUsePowerup();

    /**
     * Ste ft. Ele
     */
    public abstract void willChoosePowerup();

    /**
     * Ste ft. Ele
     */
    public abstract void didChoosePowerup();

    /**
     * Ste ft. Ele
     */
    public abstract void willChoosePowerupEffect();

    /**
     * Ste ft. Ele
     */
    public abstract void didChoosePowerupEffect();
}
