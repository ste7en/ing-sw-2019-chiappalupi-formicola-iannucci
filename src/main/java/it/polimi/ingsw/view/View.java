package it.polimi.ingsw.view;

import it.polimi.ingsw.model.cards.Damage;
import it.polimi.ingsw.model.player.User;
import it.polimi.ingsw.model.utility.PlayerColor;
import it.polimi.ingsw.networking.Client;
import it.polimi.ingsw.networking.utility.CommunicationMessage;
import it.polimi.ingsw.networking.utility.ConnectionType;
import it.polimi.ingsw.utility.AdrenalineLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observer;

import static it.polimi.ingsw.networking.utility.CommunicationMessage.*;


/**
 * View class of the MVC paradigm.
 * It's abstract because several methods will be either overridden or implemented in its children classes.
 *
 * @author Daniele Chiappalupi, Stefano Formicola, Elena Iannucci
 */
public abstract class View implements Observer{

    protected Client client;

    private PlayerColor playerColor;

    /**
     * Log strings
     */
    protected static String APPLICATION_STARTED   = "Adrenaline application started. View instance created.";
    protected static String DID_ASK_CONNECTION    = "Connection parameters chosen: ";
    protected static String ONLOGIN_FAILURE       = "Username creation failed.";
    protected static String ONLOGIN_SUCCESS       = "Username logged in.";
    protected static String JOIN_WAITING_ROOM     = "Joining the waiting room...";
    protected static String DID_JOIN_WAITING_ROOM = "";
    protected static String DID_CHOOSE_WEAPON     = "Weapon chosen: ";


    public abstract void onViewUpdate();

    public abstract void onFailure();

    public abstract void onStart();

    /**
     * Public method implemented by subclasses when starting a new application instance.
     * The user will be prompted to choose the connection parameters.
     */
    protected abstract void willChooseConnection();

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
     * Abstract method implemented by subclasses and used to create a new user
     * logged to the server.
     */
    protected abstract void login();

    /**
     * Called to notify a login failure
     */
    public void onLoginFailure() {
        AdrenalineLogger.error(ONLOGIN_FAILURE);
        login();
    }

    /**
     * Called to notify a successful login
     * @param username username
     */
    public void onLoginSuccess(String username) {
        AdrenalineLogger.success(ONLOGIN_SUCCESS);
        joinWaitingRoom(username);
    }

    /**
     * Called to join the server's waiting room for a new game
     * @param username username
     */
    private void joinWaitingRoom(String username) {
        AdrenalineLogger.info(JOIN_WAITING_ROOM);
        var args = new HashMap<String, String>();
        args.put(User.username_key, username);
        this.client.send(CommunicationMessage.from(0, USER_LOGIN, args));
    }

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
     * Public method implemented by subclasses when choosing a weapon.
     * The user will be prompted to choose which weapon he wants to use.
     *
     * @param weapons it's an ArrayList containing all of the weapons that the player can use.
     */
    public abstract void willChooseWeapon(ArrayList<String> weapons); {

    }

    /**
     * Called when the Weapon to be used is chosen.
     *
     * @param weaponSelected it's the weapon that has been selected by the player.
     */
    public void didChooseWeapon(String weaponSelected) {
        AdrenalineLogger.info(DID_CHOOSE_WEAPON + weaponSelected);
        var args = new HashMap<String, String>();
        args.put("0", weaponSelected);
        this.client.send(CommunicationMessage.from(0, WEAPON_TO_USE, args));
    }

    /**
     * Dani
     */
    public abstract void willChooseDamage(ArrayList<ArrayList<Damage>> possibleDamages);

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
